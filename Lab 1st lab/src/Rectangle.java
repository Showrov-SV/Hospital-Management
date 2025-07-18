public class Rectangle extends Shape implements Draw{
private int height;
private int width;
public Rectangle(int location ,int height, int width){
    super(location);
    this.height = height;
    this.width= width;
}
public void display(){
    System.out.println("This is a Rectangle! \nLocation:"+location+"\n and Height:"+height+" Width:"+width);
}

public void start(){
        System.out.println("Start drawing Rectangle");

}
public void stop(){
        System.out.println("Stop drawing Rectangle");

}
}
