package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import com.sun.opengl.util.texture.*;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javax.vecmath.Vector3f;

public class MyGraphics {

	public void MyGraphics() {

	}

	public void DrawLine(GL gl, GLU glu, MyGeometry geometry, float size) {
		// FIXME: Should test that vertices is initialized before to draw
		gl.glLineWidth(size);
		gl.glBegin(GL.GL_LINES);
		for (int j = 0; j < geometry.vertices.length - 1; j++) {
			gl.glVertex3f((float) ((geometry.vertices[j].x)),
					(float) ((geometry.vertices[j].y)),
					(float) ((geometry.vertices[j].z)));
			gl.glVertex3f((float) ((geometry.vertices[j + 1].x)),
					(float) ((geometry.vertices[j + 1].y)),
					(float) ((geometry.vertices[j + 1].z)));
		}
		gl.glEnd();

	}

	public void DrawCircle(GL gl, GLU glu, float x, float y, float z,
			int numPoints, float radius) {

		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (x + (Math.cos(angle)) * radius);
			tempPolygon[k][1] = (float) (y + (Math.sin(angle)) * radius);
			tempPolygon[k][2] = z;
		}

		for (int k = 0; k < numPoints; k++) {
			glu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glLineWidth(1.1f);
		gl.glBegin(GL.GL_LINES);
		float xBegin, xEnd, yBegin, yEnd;
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);
			xBegin = (float) (x + (Math.cos(angle)) * radius);
			yBegin = (float) (y + (Math.sin(angle)) * radius);
			angle = (float) ((k + 1) * 2 * Math.PI / numPoints);
			xEnd = (float) (x + (Math.cos(angle)) * radius);
			yEnd = (float) (y + (Math.sin(angle)) * radius);
			gl.glVertex3f(xBegin, yBegin, z);
			gl.glVertex3f(xEnd, yEnd, z);
		}
		gl.glEnd();

	}

	public void DrawGeometry(GL gl, GLU glu, MyGeometry geometry, float z_offset) {

		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

		int curPolyGonNumPoints = geometry.vertices.length;
		double tempPolygon[][] = new double[curPolyGonNumPoints][3];

		// Convert vertices as a list of double for
		// gluTessVertex
		for (int j = 0; j < curPolyGonNumPoints; j++) {
			tempPolygon[j][0] = (float) (geometry.vertices[j].x);
			tempPolygon[j][1] = (float) (geometry.vertices[j].y);
			tempPolygon[j][2] = (float) (geometry.vertices[j].z + z_offset);
		}

		for (int j = 0; j < curPolyGonNumPoints; j++) {
			glu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}
		// gl.glNormal3f(0.0f, 1.0f, 0.0f);

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

		// FIXME: This add a black line around the polygon.
		// For a better visual quality but we should check the cost of it.
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		this.DrawLine(gl, glu, geometry, 1.0f);

	}
	
	public void DrawTexturedQuad(GL gl, GLU glu, MyImage img, float z_offset){

		
		
	gl.glColor3f(1.0f, (float) (Math.random()*1.0f), 0.0f);
	
//	TextureCoords textureCoords;
//    textureCoords = img.texture.getImageTexCoords();
//    float textureTop = textureCoords.top();
//    float textureBottom = textureCoords.bottom();
//    float textureLeft = textureCoords.left();
//    float textureRight = textureCoords.right();
//    
//    img.texture.bind();
//
//gl.glBegin(GL_QUADS);
//	      // Front Face
//	      gl.glTexCoord2f(textureLeft, textureBottom);
//	      gl.glVertex3f(img.x, -(img.y+img.image.getHeight()), z_offset); // bottom-left of the texture and quad
//	      gl.glTexCoord2f(textureRight, textureBottom);
//	      gl.glVertex3f((img.x+img.image.getWidth()), -(img.y+img.image.getHeight()), z_offset); // bottom-right of the texture and quad
//	      gl.glTexCoord2f(textureRight, textureTop);
//	      gl.glVertex3f((img.x+img.image.getWidth()), -(img.y), z_offset); // top-right of the texture and quad
//	     gl.glTexCoord2f(textureLeft, textureTop);
//	      gl.glVertex3f(img.x, -img.y, z_offset); // top-left of the texture and quad	      
//	      gl.glEnd();
		
	}

	public void Draw3DQuads(GL gl, GLU glu, MyGeometry geometry, float z_offset) {
		int curPolyGonNumPoints = geometry.vertices.length;
		for (int j = 0; j < curPolyGonNumPoints; j++) {
			int k = (j + 1) % curPolyGonNumPoints;
			gl.glBegin(GL_QUADS);
			if (j == 3) {
				gl.glNormal3f(0.0f, 0.0f, 1.0f);
			}
			if (j == 0) {
				gl.glNormal3f(-1.0f, 0.0f, 0.0f);
			}
			if (j == 1) {
				gl.glNormal3f(0.0f, 0.0f, -1.0f);
			}

			if (j == 2) {
				gl.glNormal3f(1.0f, 0.0f, 0.0f);
			}

			Vertex[] vertices = new Vertex[4];
			for (int i = 0; i < 4; i++) {
				vertices[i] = new Vertex();
			}
			vertices[0].x = geometry.vertices[j].x;
			vertices[0].y = geometry.vertices[j].y;
			vertices[0].z = geometry.vertices[j].z + z_offset;

			vertices[1].x = geometry.vertices[k].x;
			vertices[1].y = geometry.vertices[k].y;
			vertices[1].z = geometry.vertices[k].z + z_offset;

			vertices[2].x = geometry.vertices[k].x;
			vertices[2].y = geometry.vertices[k].y;
			vertices[2].z = geometry.vertices[k].z;

			vertices[3].x = geometry.vertices[j].x;
			vertices[3].y = geometry.vertices[j].y;
			vertices[3].z = geometry.vertices[j].z;

			// Compute the normal of the quad
			Vector3f normal = new Vector3f(0.0f, 0.0f, 0.0f);

			for (int i = 0; i < 4; i++) {
				int i1 = (i + 1) % 4;
				normal.x += (vertices[i].y - vertices[i1].y)
						* (vertices[i].z + vertices[i1].z);
				normal.y += (vertices[i].z - vertices[i1].z)
						* (vertices[i].x + vertices[i1].x);
				normal.z += (vertices[i].x - vertices[i1].x)
						* (vertices[i].y + vertices[i1].y);
			}
			normal.normalize(normal);
			// FIXME: The normal is not well computed.
			// gl.glNormal3f((float)normal.x, (float)normal.y, (float)normal.z);
			gl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
			gl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
			gl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
			gl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

			gl.glEnd();
		}

	}
	
	public void DrawOpenGLHelloWorldShape(GL gl) {

		float red = (float) (Math.random()) * 1;
		float green = (float) (Math.random()) * 1;
		float blue = (float) (Math.random()) * 1;

		gl.glColor3f(red, green, blue);
		// ----- Render a quad -----

		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
	}

}
