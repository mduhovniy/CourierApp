package info.duhovniy.courierapp.data;

import java.util.Arrays;
import java.util.Queue;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.CYAN;
import static android.graphics.Color.DKGRAY;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.MAGENTA;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;


public class MarkerColor {

    private Queue<Integer> colors;

    public MarkerColor() {
        colors.addAll(Arrays.asList(BLACK, BLUE, CYAN, DKGRAY, GRAY, GREEN, MAGENTA, RED, YELLOW));
    }

    public int getColor() {
        int color = colors.remove();
        colors.add(color);
        return color;
    }
}

