import processing.core.*; 
import processing.xml.*; 

import org.openkinect.*; 
import org.openkinect.processing.*; 
import hypermedia.video.*; 
import processing.video.*; 
import java.awt.*; 
import fullscreen.*; 
import japplemenubar.*; 
import java.util.*; 
import damkjer.ocd.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class chaosBG extends PApplet {










///////////////////////////////////////////////////////////
chaosBG								that;
FullScreen 							fullScreen;
KinectTracker 						tracker;

///////////
int 								elementCount = 6000;
int 								depth = 10;
float								globalFriction=0.8f;
boolean								kinect = false;
///////////

ArrayList<CollisionElement> 		elements = new ArrayList();
ArrayList<LorenzElement> 			lorenzElements = new ArrayList();
CollisionDetection					collisionDetection;
CollisionElement					element;
NewChaosElement						elementN;
LorenzElement						elementL;
int									globalDisturbance=0;

			
PVector								mousePos=new PVector(mouseX,mouseY);
float								mouseMoved;
boolean								movement=false;
boolean								lorenzMovement=false;

float								blobStart,blobFrames;
float[]								blobs = {0,0};

boolean								blobPressed=false;
float								blobMoved=10;
MouseElement						mouseElement=null;


///////////////////////////////////////////////////////////
public void setup() {
	that = this;
	size(1280,970,P3D);
	background(255);
	stroke(0);
	frameRate(25);
	noFill();

	elementCount=PApplet.parseInt(map(width*height, 0,1680*1050 ,0, elementCount));
	depth=PApplet.parseInt(map(width*height, 0,1680*1050 ,0, depth));

	if(kinect) tracker = new KinectTracker(this);
	fullScreen = new FullScreen(this); 
//	fullScreen.enter(); 
  	
	

//	Create Elements
	for (int i=0; i<elementCount; i++) {
		element=new NewChaosElement(this);
		elements.add(element);
	}
	collisionDetection = new CollisionDetection(that, elements);
}


///////////////////////////////////////////////////////////
public void draw() {
	println(frameRate);
	translate(0,0,depth);
	background(255);
	if(kinect) blobs=tracker.calculateBlobs();
	environment();
	movement=false;
	lorenzMovement=false;
	mouseElement();
	
	collisionDetection.mapElements();
	

//	Mouse 'n' Blobs
	Iterator itr0 = lorenzElements.iterator();
	for(int i=lorenzElements.size()-1; i>=0; i--) {
		elementL= (LorenzElement) lorenzElements.get(i);
		if(elementL.moved==false) elementL.allSet=true;
		elementL.moved=false;
		collisionDetection.testElement(elementL);
		elementL.move();
	}
//	Collide the shit out of it.
	Iterator itr = elements.iterator(); 
	while(itr.hasNext()) {
		element= (CollisionElement)itr.next();
		collisionDetection.testElement(element);
	}

//	Move!
	Iterator itr2 = elements.iterator(); 
	while(itr2.hasNext()) {
		elementN= (NewChaosElement)itr2.next();
		elementN.move();
		if(elementN.lorenz==null ) frame(elementN);
		elementN.lorenz=null;
	}
	

}
///////////////////////////////////////////////////////////
public void mouseElement() {
	if(mousePressed && mouseElement==null) {
		mouseElement =new MouseElement(that);
		collisionDetection.addElement(mouseElement);
//		globalDisturbance=int(random(0,3));
	}
	else if(mousePressed && mouseElement != null) {
		mouseElement.move();
	}
	else if(!mousePressed && mouseElement != null) {
		mouseElement.finalize();
		collisionDetection.elements.remove(mouseElement);
		mouseElement = null;
	}
}
///////////////////////////////////////////////////////////
public void environment() {
//	Mouse
	mouseMoved=PVector.dist(mousePos,new PVector(mouseX, mouseY));
	mousePos=new PVector(mouseX,mouseY);
	if(mouseMoved > 0) movement=true;

//	friction
	if(!movement && !lorenzMovement && globalFriction > 0.0f) globalFriction-=0.005f;
	else if((movement || lorenzMovement) && ((globalFriction <= 0.85f && !lorenzMovement) || globalFriction <= 0.4f)) globalFriction+=0.08f;

// Blob
	blobPressed = (PApplet.parseInt(blobs[0]) > 0 && PApplet.parseInt(blobs[1]) > 0);
	if(!blobPressed) blobStart=frameCount;
	if(blobPressed) blobFrames=frameCount-blobStart;
	else blobFrames=0;
	
	globalDisturbance--;	
}
///////////////////////////////////////////////////////////
public void frame(NewChaosElement element) {
	NewChaosElement thisElement = (NewChaosElement) element;
	
	float border =  30;
	if(element.location.x < 0-border) {
		element.location.x*=-1;
//			element.velocity.x*=-1;
//			element.velocity.add(new PVector(0,force/2,0));
	}
	else if(element.location.x > width+border) {
		element.location.x= width+border-(element.location.x-width+border);
//			element.velocity.x= width+border-(element.velocity.x-width+border);
//			element.velocity.add(new PVector(-force/2,0,0));
	}
	if(element.location.y < 0-border) {
		element.location.y*=-1;
//			element.velocity.y*=-1;
//			element.velocity.add(new PVector(0,force/2,0));
	}
	else if(element.location.y > height+border) {
		element.location.y= height+border-(element.location.y-height+border);
//			element.velocity.y= height+border-(element.velocity.y-height+border);
//			element.velocity.add(new PVector(0,-force/2,0));
	}
	if(element.location.z < 0-border) {
		element.location.z*=-1;
//			element.velocity.z*=-1;
//			element.velocity.add(new PVector(0,0,force/2));
	}
	else if(element.location.z > depth) {
		element.location.z= depth-(element.location.z-depth);
//			element.velocity.z= maxZ-(element.velocity.z-maxZ);
//			element.velocity.add(new PVector(0,0,-force/2));
	}
}	
public void stop() {
    that.tracker.kinect.quit();
  }
class CollisionDetection {
	chaosBG								that;
	CollisionDetection					nextDetection;
	
	ArrayList<CollisionMap>				maps = new ArrayList();
	ArrayList<CollisionElement>			elements = new ArrayList();
	
	CollisionMap						map;
	CollisionElement 					element;
///////////////////////////////////////////////////////////
	CollisionDetection(chaosBG that_,ArrayList<CollisionElement> elements_) {
		that=that_;
		elements=(ArrayList<CollisionElement>) elements_.clone();
		nextDetection= new CollisionDetection(that, true);
	}
	CollisionDetection(chaosBG that_) {
		that=that_;
		nextDetection= new CollisionDetection(that, true);
	}
	CollisionDetection(chaosBG that_, boolean flag) {
		that=that_;
		nextDetection=this;
	}
///////////////////////////////////////////////////////////
	public void addElement (CollisionElement element) {
		this.elements.add(element);
		addToMap(element);
	}
///////////////////////////////////////////////////////////
	public void addToMap(CollisionElement element) {
		Iterator itr= maps.iterator();
		while(itr.hasNext()) {
			map=(CollisionMap) itr.next();
			if(map.gridSize!=element.actionRadius) continue;
			map.add(element);
			return;			
		}
		map=new CollisionMap(that, nextDetection, element.actionRadius);
		map.add(element);	
		maps.add(map);
	}
///////////////////////////////////////////////////////////
	public int mapSize() {
		int length=0;
		Iterator itr= maps.iterator();
		while(itr.hasNext()) {
			map=(CollisionMap) itr.next();
			length+=map.size();
		}
		return length;
	}
///////////////////////////////////////////////////////////
	public void mapElements() {
		if(nextDetection.mapSize() == elements.size()) {
			maps=(ArrayList<CollisionMap>)nextDetection.maps.clone();		
		}
		else {
			maps=new ArrayList();
			Iterator itr= elements.iterator();
			while(itr.hasNext()) {
				element = (CollisionElement) itr.next();
				addToMap(element);
			}
		}
		nextDetection.maps= new ArrayList();
		nextDetection.elements = new ArrayList();
	}
///////////////////////////////////////////////////////////
	public void testElement (CollisionElement element) {
		Iterator itr= maps.iterator();
		while(itr.hasNext()) {
			map=(CollisionMap) itr.next();
			map.test(element);
		}
		nextDetection.addElement(element);
	}
///////////////////////////////////////////////////////////
	public void removeElement (CollisionElement element) {
		elements.remove(element);
		mapElements();
	}
}
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
class CollisionMap {
	chaosBG								that;
	CollisionDetection					nextDetection;

	ArrayList<CollisionElement>[][] 	quadrants;
	int									columns;
	int									rows;

	float 								gridSize; 
	CollisionElement					testElement;
///////////////////////////////////////////////////////////
	int									x,y,x1,y1,x2,y2;
	PVector								quadrant,quadrant1,quadrant2,radius;

///////////////////////////////////////////////////////////
	CollisionMap(chaosBG that_, CollisionDetection nextDetection_, float gridSize_) {
		that=that_;
		nextDetection=nextDetection_;
		gridSize=gridSize_;
		columns=PApplet.parseInt(ceil(width/gridSize))+2;
		rows=PApplet.parseInt(ceil(height/gridSize))+2;
    	quadrants= (ArrayList<CollisionElement>[][]) new ArrayList[columns][rows]; 
	}
///////////////////////////////////////////////////////////
	public void add(CollisionElement element) {	
		PVector quadrant = getQuadrant(element.location);
		int x= PApplet.parseInt(quadrant.x);
		int y= PApplet.parseInt(quadrant.y);
		
		if(quadrants[x][y] == null) quadrants[x][y] = new ArrayList();
		quadrants[x][y].add(element);
	}
///////////////////////////////////////////////////////////
	public PVector getQuadrant(PVector location) {
		int x = 1+PApplet.parseInt(floor(location.x/gridSize));
		int y = 1+PApplet.parseInt(floor(location.y/gridSize));
		
		if(x < 1) x=0;
		if(x > PApplet.parseInt(ceil(width/gridSize))) x=PApplet.parseInt(ceil(width/gridSize))+1;
		if(y < 1) y=0;
		if(y > PApplet.parseInt(ceil(height/gridSize))) y=PApplet.parseInt(ceil(height/gridSize))+1;
			
		return new PVector(x,y);	
	}
///////////////////////////////////////////////////////////
	public int size() {
		int length=0;
		for(int i=0; i< quadrants.length; i++)
			for(int k=0; k< quadrants[i].length; k++)
				if(quadrants[i][k] != null)
					length+=quadrants[i][k].size();
		return length;	
	}
///////////////////////////////////////////////////////////
	public void test(CollisionElement element) {
		quadrant = getQuadrant(element.location);		
		x= PApplet.parseInt(quadrant.x);
		y= PApplet.parseInt(quadrant.y);
		x1= x-1;
		y1= y-1;
		x2= x+1;
		y2= y+1;
		if(element.actionRadius > gridSize) {
			radius = new PVector(element.actionRadius, element.actionRadius);
			quadrant1 = getQuadrant(PVector.sub(element.location, radius));		
			quadrant2 = getQuadrant(PVector.add(element.location, radius));		
		
			x1= PApplet.parseInt(quadrant1.x)-1;
			y1= PApplet.parseInt(quadrant1.y)-1;
			x2= PApplet.parseInt(quadrant2.x)+1;
			y2= PApplet.parseInt(quadrant2.y)+1;
		}

		if(quadrants[x][y] != null && quadrants[x][y].contains(element))
			quadrants[x][y].remove(element);
				
		if(x <= 1 || x >= rows-2 || y <= 1 || y >= columns-2) 
			element.frameCollision();
		
		for(int i=x1; i<=x2; i++) 
			if(i >=0 && i < quadrants.length)
				for(int k=y1; k<=y2; k++) 
					if(k >=0 && k < quadrants[i].length && quadrants[i][k] != null) {
						Iterator itr=quadrants[i][k].iterator();
						while(itr.hasNext()) {
							testElement= (CollisionElement) itr.next();
							if(element.equals(testElement)) continue;
							element.collision(testElement, this, true);
							testElement.collision(element, this,false);
						}
					}
	}
}
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
abstract class CollisionElement {
	chaosBG								that;
	PVector 							location = new PVector(0,0,0);
	PVector								velocity = new PVector(0,0,0);
	float 								actionRadius;

///////////////////////////////////////////////////////////
	public abstract void frameCollision();
	public abstract void move();
	public abstract void collide(NewChaosElement element, CollisionMap collisionMap, boolean mainCollision);
	public abstract void collide(MouseElement element, CollisionMap collisionMap, boolean mainCollision);
	public abstract void collide(LorenzElement element, CollisionMap collisionMap, boolean mainCollision);
	

///////////////////////////////////////////////////////////
	public void setActionRadius(float actionRadius_) {
		actionRadius=actionRadius_;
	}
	
	public void collision(CollisionElement element, CollisionMap collisionMap, boolean mainCollision) {
		String type=element.getClass().getName();
		
		if(type == "chaosBG$NewChaosElement") 	collide((NewChaosElement) element, collisionMap, mainCollision);
		if(type == "chaosBG$MouseElement") 		collide((MouseElement) element, collisionMap, mainCollision);
		if(type == "chaosBG$LorenzElement") 	collide((LorenzElement) element, collisionMap, mainCollision);
	}	
}
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////

class KinectTracker {
 	OpenCV opencv;
	chaosBG that;
	Kinect kinect;
  // Size of kinect image
  int kw = 640;
  int kh = 480;
  int threshold = 600;

  // Raw location
  PVector loc;

  // Interpolated location
  PVector lerpedLoc;

  // Depth data
  int[] depth;


  PImage display;

  KinectTracker(chaosBG that_) {
 	that=that_;
	opencv = new OpenCV(that);
	opencv.allocate(kw, kh);
  	kinect = new Kinect(that);
 	kinect.start();
    kinect.enableDepth(true);

    // We could skip processing the grayscale image for efficiency
    // but this example is just demonstrating everything
    kinect.processDepthImage(true);

    display = createImage(kw, kh, PConstants.RGB);
  }

  public void createDisplay() {
    PImage img = kinect.getDepthImage();
    depth = kinect.getRawDepth();

    // Being overly cautious here
    if (depth == null || img == null) return;

    // Going to rewrite the depth image to show which pixels are in threshold
    // A lot of this is redundant, but this is just for demonstration purposes
    display.loadPixels();
    for (int x = 0; x < kw; x++) {
      for (int y = 0; y < kh; y++) {
        // mirroring image
        int offset = kw-x-1+y*kw;
        // Raw depth
        int rawDepth = depth[offset];

        int pix = x+y*display.width;
        if (rawDepth < threshold) {
          // A red color instead
          display.pixels[pix] = color(255);
        } 
        else {
          display.pixels[pix] = color(0);
        }
      }
    }
    display.updatePixels();

    // Draw the image
 //   image(display, 0, 0);
  }



  public float[] calculateBlobs() {
  	float 	sumX=0,
  			sumY=0;
  	int		count=1;
  
    createDisplay();
    opencv.copy(display);
//   	image( opencv.image(), 640, 0 );
    opencv.threshold( 80 );
    Blob[] blobs = opencv.blobs( 10, width*height/2, 100, true, OpenCV.MAX_VERTICES*4 );
	
   // draw blob results
        for( int i=0; i<blobs.length; i++ ) {
           for( int j=0; j<blobs[i].points.length; j++ ) {
           		sumX+=blobs[i].points[j].x;
           		sumY+=blobs[i].points[j].y;
           		count++;
        			
           }
       }
       
      
    float[] value = {sumX/count, sumY/count};
    return value;
  }
}
class LorenzElement extends CollisionElement {
	float 							defaultRadius=200;
	int								variableSets=19;

	float							pushForce=5;

	int								startFrame;

	float							x,y,z;
	float[] 						variable= {-1.11f, 1.12f, 4.49f, 0.13f, 1.4f, 0.4f, 0.13f, 1.47f, 0.13f};
	float[][] 						points;
	PVector							average= new PVector(0,0,0);
	float							zoom=35;
	float							rotation=0;
	int								iterations=1000;
	
	ArrayList<CollisionElement> 	elements = new ArrayList();
	
	PVector							quadrant = new PVector(0,0);

	boolean							allSet=false;
	int								count=0;	
	boolean							moved=true;
///////////////////////////////////////////////////////////
	LorenzElement(chaosBG that_, PVector location_, float actionRadius_) {
		that=that_;
		actionRadius=actionRadius_;
		location = location_;
		startFrame=frameCount;
		
		getVariableSet();
		generatePoints();
	}
	LorenzElement(chaosBG that_, PVector location_) {
		that=that_;
		actionRadius=defaultRadius;
		location = location_;
		startFrame=frameCount;

		getVariableSet();
		generatePoints();
	}
///////////////////////////////////////////////////////////
	public void frameCollision() {}
///////////////////////////////////////////////////////////
	public void collide(NewChaosElement element, CollisionMap collisionMap,  boolean mainCollision) {
		float distance=PVector.dist(new PVector(location.x,location.y),new PVector(element.location.x, element.location.y));
	
		if(!allSet) {	
			PVector thisQuadrant= collisionMap.getQuadrant(element.location);
			
			if(distance<=actionRadius && elements.size() < iterations && !thisQuadrant.equals(quadrant)) elements.add(element);
				
			quadrant=thisQuadrant;
			return;
		}
		
		PVector newVelocity= PVector.sub(element.location,location);
		newVelocity.normalize();
		newVelocity.mult(map(distance, 0,actionRadius,pushForce,0));		
			
		velocity.add(newVelocity);
		
		
 	}
///////////////////////////////////////////////////////////
	public void collide(LorenzElement element, CollisionMap collisionMap, boolean mainCollision) {}
	public void collide(MouseElement element, CollisionMap collisionMap, boolean mainCollision) {}
	public void move() {
		count = (frameCount-startFrame)*100;
		if(count> iterations) count = iterations;
		
		if(count!=iterations) moved=true;
		
		if(!allSet) return;
		
		draw();
		
//		velocity.y-=1;
		
		velocity.limit(5);
		
		if(velocity.mag() >0) that.lorenzMovement=true;
		
		location.add(new PVector(velocity.x,velocity.y));
		
		if(location.y < -200) remove();
	}
	
	public void remove(){
		that.lorenzElements.remove(this);
		that.collisionDetection.elements.remove(this);
	}
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
	public void getVariableSet () {
		String filenumber = nf(PApplet.parseInt(random(0, variableSets)),5,0);
		String filename = sketchPath("variableSets"+java.io.File.separator+filenumber+"_lorenzVariableSet.txt");
		String[] data=loadStrings(filename);
		
		if(data==null) return;

		int id=0;
		int k=0;
		for(int i=0; 18 >i; i++) {
			if(i%2==0) {
				id++;
				continue;
			}
			variable[k]=PApplet.parseFloat(data[id]);
			k++;
			id++;
		}
		
		id++; //obj.iteration=data.readFloat();
		id+=2; //obj.zoom=data.readFloat();
		id+=2; //obj.rotation[0]=data.readFloat();
		id+=2; //obj.rotation[1]=data.readFloat();
		id+=2; //obj.rotation[2]=data.readFloat();
	}
///////////////////////////////////////////////////////////
	public void generatePoints(){
		points = new float[iterations][3];
		x=1;
		y=1;
		z=1;
		float count=0;
		for(int i=0; i<20; i++) iterate();
		for(int i=0; i<iterations; i++) {
			points[i][0]=x;
			points[i][1]=y;
			points[i][2]=z;		
			
			average.add(new PVector(x,y,z));
			iterate();	
			count++;
		}
		average.mult(1/count);
		average.mult(zoom);
	}
///////////////////////////////////////////////////////////
	public void iterate(){
		float	dx = (variable[0] * x - y * y - z * z + variable[1] * variable[2]) * variable[3],
				dy = (-y + x * y - variable[4] * x * z + variable[5]) * variable[6],
				dz = (-z + variable[7] * x * y + x * z) * variable[8];
		x+=dx;
		y+=dy;
		z+=dz;
	}
///////////////////////////////////////////////////////////
	public void draw() {
		pushMatrix();
			stroke(0);
			
			translate(location.x, location.y,location.z);
			rotation+=0.1f;
			rotateY(rotation);
			box(10);
			pushMatrix();			
				translate(-average.x, -average.y,-average.z);
				box(10);
				beginShape();
					for(int i=0; i< points.length; i++) {
						x =points[i][0];
						y =points[i][1];
						z =points[i][2];
		
						curveVertex(x*zoom,y*zoom,z*zoom);
					}
				endShape();
			popMatrix();
		popMatrix();
	}
}
class MouseElement extends CollisionElement {
	float 							defaultRadius=200;

	int								startFrame;
	
	float							moved;
	
	LorenzElement					lorenzElement;
		
	boolean							shapeSet;
	
//	PROGRAMM
	PVector newLocation;
///////////////////////////////////////////////////////////
	MouseElement(chaosBG that_, float actionRadius_) {
		that=that_;
		actionRadius=actionRadius_;
		location = new PVector (mouseX, mouseY,0);
		startFrame=frameCount;

		lorenzElement= new LorenzElement(that, location);
	}
	MouseElement(chaosBG that_) {
		that=that_;
		actionRadius=defaultRadius;
		location = new PVector (mouseX, mouseY,0);
		startFrame=frameCount;

		lorenzElement= new LorenzElement(that, location);
	}
///////////////////////////////////////////////////////////
	public void frameCollision() {}
	public void collide(NewChaosElement element, CollisionMap collisionMap, boolean mainCollision) {}
	public void collide(MouseElement element, CollisionMap collisionMap, boolean mainCollision) {}
	public void collide(LorenzElement element, CollisionMap collisionMap, boolean mainCollision) {}
///////////////////////////////////////////////////////////
	public void move() {
		PVector newLocation =new PVector (mouseX, mouseY,0);
		moved=PVector.dist(newLocation, location);
		
		if(moved>0) that.movement=true;
		
		if(frameCount-startFrame > 100 && random(0,1) >0.9f) startFrame=frameCount;

		location = new PVector (mouseX, mouseY,0);
	
		
		if(shapeSet && (moved<=0 || (PVector.dist(location, lorenzElement.location) > 80 && !lorenzElement.allSet))) {
			startFrame=frameCount;
			shapeSet=false;
			lorenzElement.remove();
		}
		if(frameCount-startFrame> 50 && !shapeSet && moved>0 ) {
			that.lorenzElements.add(lorenzElement);
			that.collisionDetection.addElement(lorenzElement);
			lorenzElement.location=new PVector(location.x,location.y, location.z);
			lorenzElement.startFrame=frameCount;
			lorenzElement.elements = new ArrayList();
			shapeSet=true;
		}
		
	}
///////////////////////////////////////////////////////////
	public void finalize() {
		if(lorenzElement.allSet) return;
	
		lorenzElement.remove();
	}
}

class NewChaosElement extends CollisionElement {

	float 							defaultRadius=50;

	float							friction=1;
	
	float							pushForce=5;
	
	int								disturbance=0;
	
	LorenzElement					lorenz=null;
	
	NewChaosElement 				element1, element2;
	///////////////////////////////////////////////////////////
	NewChaosElement(chaosBG that_, float actionRadius_) {
		that=that_;
		actionRadius=actionRadius_;
		location = new PVector (random(width), random(height),random(that.depth));
	}
	NewChaosElement(chaosBG that_) {
		that=that_;
		actionRadius=defaultRadius;
		location = new PVector (random(width), random(height),random(that.depth));
	}
	
///////////////////////////////////////////////////////////
	public void collide (NewChaosElement element, CollisionMap collisionMap, boolean mainCollision) {
		PVector newVelocity;
		float distance=PVector.dist(location, element.location);
		if(distance > actionRadius) return;
		if(lorenz!=null && !lorenz.allSet) {
			int index = lorenz.elements.indexOf(this);
			PVector pointLocation = new PVector(lorenz.points[index][0],lorenz.points[index][1],lorenz.points[index][2]);
			pointLocation.mult(lorenz.zoom);
			pointLocation.sub(lorenz.average);
			pointLocation.add(lorenz.location);
			if(PVector.dist(location, pointLocation)<=random(10, 20)) return;
		}
		if(distance < actionRadius*0.9f) {
			newVelocity= PVector.sub(location,element.location);
		
			if(mainCollision) {
				float lineZ= abs((location.z-element.location.z)/2);
				stroke(map(lineZ,0,30,0,100 ));
				line(location.x,location.y,location.z,element.location.x,element.location.y,element.location.z);
			}			
		}
		else {
			newVelocity= PVector.sub(element.location,location);
		}
		newVelocity.normalize();
		newVelocity.mult(map(distance, 0,actionRadius,pushForce,0));		
			
		velocity.add(newVelocity);
		
//		println(element1.velocity);
	}
///////////////////////////////////////////////////////////
	public void collide(MouseElement element, CollisionMap collisionMap, boolean mainCollision) {
		float distance=PVector.dist(new PVector(location.x,location.y),new PVector(element.location.x, element.location.y));
		
		if(element.moved <=0 || distance>element.actionRadius || that.globalFriction < 0.8f) return;
		
		int pressedFrames = frameCount-element.startFrame;

		PVector newVelocity= PVector.sub(location,element.location);
		newVelocity.normalize();
		newVelocity.mult(0.5f*pressedFrames);
		newVelocity.limit(5);
		
		velocity.add(newVelocity);
		
		friction=0.7f;
		disturbance=PApplet.parseInt(random(0,2));
	}
///////////////////////////////////////////////////////////
	public void collide(LorenzElement element, CollisionMap collisionMap, boolean mainCollision) {
		if(!element.allSet) {
			if(element.elements.contains(this)) lorenz= element;	
			return;
		}
			
		float distance=PVector.dist(new PVector(location.x,location.y),new PVector(element.location.x, element.location.y));
		
		if(distance>element.actionRadius) return;
		
		PVector newVelocity= PVector.sub(location,element.location);
		newVelocity.normalize();
		newVelocity.mult(10);
		
		velocity.add(newVelocity);
		
		friction=0.7f;
		disturbance=PApplet.parseInt(random(0,2));
	}
///////////////////////////////////////////////////////////
	public void move() {	
		if(lorenz !=null) {
  			int index = lorenz.elements.indexOf(this);
			if(!lorenz.allSet && index<=lorenz.count) {
				moveLorenz(index);
				return;
			}
		}
		moveNormal();
	}
///////////////////////////////////////////////////////////
	public void moveLorenz(int index){
		PVector pointLocation = new PVector(lorenz.points[index][0],lorenz.points[index][1],lorenz.points[index][2]);
		pointLocation.mult(lorenz.zoom);
		pointLocation.sub(lorenz.average);
		pointLocation.add(lorenz.location);
		
		velocity= PVector.sub(pointLocation, location);
		
		velocity.mult(0.3f);
	//	stroke(255,0,0);
		if(velocity.mag() <=1) {
			if(index-1 >=0 && index+2 < lorenz.elements.size()) {
				element =(NewChaosElement) lorenz.elements.get(index-1);
				
				
				element1 =(NewChaosElement) lorenz.elements.get(index+1);
				element2 =(NewChaosElement) lorenz.elements.get(index+2);
				line(element.location.x, element.location.y, element.location.z, location.x,location.y,location.z);
//				curve( element.location.x, element.location.y, element.location.z,location.x,location.y,location.z, element1.location.x, element1.location.y, element1.location.z, element2.location.x, element2.location.y, element2.location.z);
			}
			if(index-2 >=0 && index+1 < lorenz.elements.size()) {
				element =(NewChaosElement) lorenz.elements.get(index+1);
					element1 =(NewChaosElement) lorenz.elements.get(index-1);
					element2 =(NewChaosElement) lorenz.elements.get(index-2);
					line(element.location.x, element.location.y, element.location.z, location.x,location.y,location.z);
//					curve( element.location.x, element.location.y, element.location.z,location.x,location.y,location.z, element1.location.x, element1.location.y, element1.location.z, element2.location.x, element2.location.y, element2.location.z);
			}
		}
		stroke(0);

	///	lorenz.moved=true;		
	
		if(PVector.dist(pointLocation, location) <=1) location = pointLocation;
		else {
			location.add(velocity);
			lorenz.moved=true;
		}
	}
///////////////////////////////////////////////////////////
	public void moveNormal() {
  		velocity.mult(that.globalFriction);
		velocity.mult(friction);
		
		if(lorenz != null && PVector.dist(location, lorenz.location) > 210) {
			velocity.mult(-1);
		}
		
		if(that.globalDisturbance > 0 || disturbance>0) {
			float mult=-0.06f;
			velocity.mult(mult);
			location.add(velocity);
			velocity.mult(1/mult);
			
		}
		else {
			location.add(velocity);
		}
		
		friction=1;
		disturbance--;
	}
///////////////////////////////////////////////////////////
	public void frameCollision() {}
///////////////////////////////////////////////////////////
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "chaosBG" });
  }
}