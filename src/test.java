import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class test {
    private static class VR{
        private String name;
        private int count;


        private VR(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public static void main(String[] args){
        List<VR> list =
                Collections.synchronizedList(new ArrayList<VR>());
        list.add(new VR("TEST", 1));
        list.add(new VR("TEST", 2));
        list.add(new VR("TEST", 3));

        System.out.println(list.get(2).getCount());


        for(int i = 0; i< list.size(); i++){
            System.out.println(list.get(i).getCount());
        }
        int x = -2;

        if(0 < x){
            System.out.println(true);
        }else {
            System.out.println(false);

        }
    }
}
