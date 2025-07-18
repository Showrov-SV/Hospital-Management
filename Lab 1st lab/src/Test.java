public class Test {
public static void main(String[] a){
    Shape ab[] =new Shape[2];
    ab[0]= new Rectangle(2, 3, 4);
    ab[1] = new Circle(3, 5.54);


    ((Draw)ab[0]).start();
    ab[0].display();
    ((Draw)ab[0]).stop();
}
}
