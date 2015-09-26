package zero.mods.zerocore.client.model.obj;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zero.mods.zerocore.client.model.ICustomModel;
import zero.mods.zerocore.client.model.ILoader;
import zero.mods.zerocore.client.model.ModelFormatException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class ObjLoader implements ILoader {

    public static ObjLoader getInstance() {

        return ObjLoader.s_instance;
    }

    public ICustomModel loadModel(ResourceLocation model, ResourceLocation texture) {

        try {

            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(model);
            ObjLoader.ParsingState state = new ObjLoader.ParsingState(new ArrayList<GroupObject>());

            ObjLoader.parseModel(resource.getInputStream(), state);

            return new ObjModel(state.GroupObjects, state.CurrentGroupObject, texture);

        } catch (Exception ex) {

            throw new ModelFormatException(String.format("Exception parsing OBJ model '%s'", model.toString()), ex);
        }
    }

    private ObjLoader() {
    }

    private static class ParsingState {

        public ParsingState(ArrayList<GroupObject> groupObjects) {

            this.GroupObjects = groupObjects;
            this.CurrentGroupObject = null;

            this.Vertices = new ArrayList<Vertex>();
            this.VertexNormals = new ArrayList<Vertex>();
            this.TextureCoordinates = new ArrayList<TextureCoordinate>();
        }

        public GroupObject CurrentGroupObject;
        public final ArrayList<GroupObject> GroupObjects;
        public final ArrayList<Vertex> Vertices;
        public final ArrayList<Vertex> VertexNormals;
        public final ArrayList<TextureCoordinate> TextureCoordinates;
    }

    private static void parseModel(InputStream inputStream, ObjLoader.ParsingState parsingState) throws ModelFormatException {

        BufferedReader reader = null;
        String currentLine = null;
        int lineCount = 0;

        try {

            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((currentLine = reader.readLine()) != null) {

                lineCount++;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();

                if (currentLine.startsWith("#") || currentLine.length() == 0)
                    continue;

                else if (currentLine.startsWith("v ")) {

                    Vertex vertex = ObjLoader.parseVertex(currentLine, lineCount, false);

                    if (vertex != null)
                        parsingState.Vertices.add(vertex);

                } else if (currentLine.startsWith("vn ")) {

                    Vertex vertex = ObjLoader.parseVertex(currentLine, lineCount, true);

                    if (vertex != null)
                        parsingState.VertexNormals.add(vertex);

                } else if (currentLine.startsWith("vt ")) {

                    TextureCoordinate textureCoordinate = ObjLoader.parseTextureCoordinate(currentLine, lineCount);

                    if (textureCoordinate != null)
                        parsingState.TextureCoordinates.add(textureCoordinate);

                } else if (currentLine.startsWith("f ")) {

                    if (parsingState.CurrentGroupObject == null)
                        parsingState.CurrentGroupObject = new GroupObject("Default");

                    Face face = parseFace(currentLine, lineCount, parsingState);

                    if (face != null)
                        parsingState.CurrentGroupObject.Faces.add(face);

                } else if (currentLine.startsWith("g ") | currentLine.startsWith("o ")) {

                    GroupObject group = ObjLoader.parseGroupObject(currentLine, lineCount);

                    if (group != null) {

                        if (parsingState.CurrentGroupObject != null)
                            parsingState.GroupObjects.add(parsingState.CurrentGroupObject);
                    }

                    parsingState.CurrentGroupObject = group;
                }
            }

            parsingState.GroupObjects.add(parsingState.CurrentGroupObject);

        } catch (IOException ex) {

            throw new ModelFormatException("IO Exception reading model format", ex);

        } finally {

            try {
                reader.close();

            } catch (NullPointerException e) {
            } catch (IOException e) {
            }

            try {
                inputStream.close();

            } catch (IOException e) {
            }
        }
    }


    private static boolean isValidLine(Pattern pattern, String line) {

        return pattern.matcher(line).matches();
    }

    private static Vertex parseVertex(String line, int lineCount, boolean isVertexNormal) throws ModelFormatException {

        Vertex vertex = null;

        if (ObjLoader.isValidLine(isVertexNormal ? ObjLoader.s_vertexNormalPattern : ObjLoader.s_vertexPattern, line)) {

            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {

                if (!isVertexNormal && (tokens.length == 2))
                    vertex = new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));

                else if (tokens.length == 3)
                    vertex = new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));


            } catch (NumberFormatException e) {

                throw ModelFormatException.newNumberFormatError(lineCount, e);
            }
        } else {

            throw ModelFormatException.newParseError(line, lineCount);
        }

        return vertex;
    }

    private static TextureCoordinate parseTextureCoordinate(String line, int lineCount) throws ModelFormatException {

        TextureCoordinate textureCoordinate = null;

        if (ObjLoader.isValidLine(ObjLoader.s_textureCoordinatePattern, line)) {

            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try {

                if (tokens.length == 2)
                    textureCoordinate = new TextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]));

                else if (tokens.length == 3)
                    textureCoordinate = new TextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));

            } catch (NumberFormatException e) {

                throw ModelFormatException.newNumberFormatError(lineCount, e);
            }
        } else {

            throw ModelFormatException.newParseError(line, lineCount);
        }

        return textureCoordinate;
    }

    private static GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException {

        GroupObject group = null;

        if (ObjLoader.isValidLine(ObjLoader.s_groupObjectPattern, line)) {

            String trimmedLine = line.substring(line.indexOf(" ") + 1);

            if (trimmedLine.length() > 0)
                group = new GroupObject(trimmedLine);

        } else {

            throw ModelFormatException.newParseError(line, lineCount);
        }

        return group;
    }

    private static Face parseFace(String line, int lineCount, ObjLoader.ParsingState parsingState) throws ModelFormatException {

        Face face;

        boolean isV_VT_VN_Line = ObjLoader.isValidLine(ObjLoader.s_face_V_VT_VN_Pattern, line);
        boolean isV_VT_Line = ObjLoader.isValidLine(ObjLoader.s_face_V_VT_Pattern, line);
        boolean isV_VN_Line = ObjLoader.isValidLine(ObjLoader.s_face_V_VN_Pattern, line);
        boolean isV_Line = ObjLoader.isValidLine(ObjLoader.s_face_V_Pattern, line);

        if (isV_VT_VN_Line || isV_VT_Line || isV_VN_Line || isV_Line) {

            face = new Face();

            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens;

            if (tokens.length == 3) {

                if (parsingState.CurrentGroupObject.GLDrawingMode == -1)
                    parsingState.CurrentGroupObject.GLDrawingMode = GL11.GL_TRIANGLES;

                else if (parsingState.CurrentGroupObject.GLDrawingMode != GL11.GL_TRIANGLES)
                    throw ModelFormatException.newInvalidFace(line, lineCount, 4, tokens.length);

            } else if (tokens.length == 4) {

                if (parsingState.CurrentGroupObject.GLDrawingMode == -1)
                    parsingState.CurrentGroupObject.GLDrawingMode = GL11.GL_QUADS;

                else if (parsingState.CurrentGroupObject.GLDrawingMode != GL11.GL_QUADS)
                    throw ModelFormatException.newInvalidFace(line, lineCount, 3, tokens.length);
            }


            if (isV_VT_VN_Line) {

                // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...

                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {

                    subTokens = tokens[i].split("/");

                    face.vertices[i] = parsingState.Vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = parsingState.TextureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = parsingState.VertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();

            } else if (isV_VT_Line) {

                // f v1/vt1 v2/vt2 v3/vt3 ...

                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {

                    subTokens = tokens[i].split("/");
                    face.vertices[i] = parsingState.Vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = parsingState.TextureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();

            } else if (isV_VN_Line) {

                // f v1//vn1 v2//vn2 v3//vn3 ...

                face.vertices = new Vertex[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i) {

                    subTokens = tokens[i].split("//");
                    face.vertices[i] = parsingState.Vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = parsingState.VertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();

            } else if (isV_Line) {

                // f v1 v2 v3 ...

                face.vertices = new Vertex[tokens.length];

                for (int i = 0; i < tokens.length; ++i)
                    face.vertices[i] = parsingState.Vertices.get(Integer.parseInt(tokens[i]) - 1);

                face.faceNormal = face.calculateFaceNormal();

            } else
                throw ModelFormatException.newParseError(line, lineCount);

        } else {
            throw ModelFormatException.newParseError(line, lineCount);
        }

        return face;
    }

    private static Pattern s_vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
    private static Pattern s_vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
    private static Pattern s_textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+(\\.\\d+)?){2,3} *$)");
    private static Pattern s_face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
    private static Pattern s_face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
    private static Pattern s_face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
    private static Pattern s_face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
    private static Pattern s_groupObjectPattern = Pattern.compile("([go]( [\\w\\d\\.]+) *\\n)|([go]( [\\w\\d\\.]+) *$)");


    private static final ObjLoader s_instance = new ObjLoader();
}
