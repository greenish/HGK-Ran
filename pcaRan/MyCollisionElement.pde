class MyCollisionElement extends CollisionElement{
	PVector							velocity = new PVector(0,0,0);
/////////////////////////////////////////////////////////
	void collide(ElementChaos element, CollisionMap collisionMap, boolean mainCollision){};
	void collide(ElementLorenz element, CollisionMap collisionMap, boolean mainCollision){};
	void collide(ElementBlob element, CollisionMap collisionMap, boolean mainCollision){};
/////////////////////////////////////////////////////////
 	void collision(CollisionElement element, CollisionMap collisionMap, boolean mainCollision) {
		String type=element.getClass().getName();
		
		if(type == "pcaRan$ElementChaos") 	collide((ElementChaos) element, collisionMap, mainCollision);
		if(type == "pcaRan$ElementLorenz") 	collide((ElementLorenz) element, collisionMap, mainCollision);
		if(type == "pcaRan$ElementBlob") 	collide((ElementBlob) element, collisionMap, mainCollision);
	}	
}