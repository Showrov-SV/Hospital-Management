public class Circle extends Shape implements Draw {
private double radius;
public Circle(int location, double radius){
    super(location);
    this.radius= radius;

}
public void display(){
    System.out.println("This is a Circle!\n Location:"+location+"\n and the radius:"+radius);
}

public void start(){
        System.out.println("Start drawing Circle");

}
public void stop(){
        System.out.println("Stop drawing Circle");

}
}
