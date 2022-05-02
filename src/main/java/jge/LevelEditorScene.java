package jge;

import jge.renderer.Shader;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;

    private float[] vertexArray = {
            // position                 // color
            // x    // y    // z        // r    // g    // b    // a
             50.5f,  -50.5f,   0.0f,       1.0f,   0.0f,   0.0f,   1.0f, // Bottom right 0
            -50.5f,   50.5f,   0.0f,       0.0f,   1.0f,   0.0f,   1.0f, // Top left     1
             50.5f,   50.5f,   0.0f,       0.0f,   0.0f,   1.0f,   1.0f, // Top right    2
            -50.5f,  -50.5f,   0.0f,       1.0f,   1.0f,   0.0f,   1.0f, // Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            /*
                    x 2      x 1    |    x 2      x
                                    |
                                    |
                    x        x 3    |    x 3      x 1
             */
            2, 1, 0, // Top right triangle
            0, 1, 3, // Bottom left triangle
    };

    Shader defaultShader = null;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-50.0f, -50.0f));

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        // ============================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ============================================================

        // Create VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // Create EBO and upload the element buffer
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {

        camera.position.x -= dt * 50.0f;

        // ============================================================
        // Use
        // ============================================================
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // ============================================================
        // Draw
        // ============================================================

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // ============================================================
        // Detach
        // ============================================================

        // Disable vertex attribute pointers
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        // Unbind the VAO we're using
        glBindVertexArray(0); // 0 means bind nothing

        defaultShader.detach();
    }
}
