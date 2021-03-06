package chart.wordcloud;

import java.util.Random;

import javafx.scene.paint.Color;

/**
 * Created by kenny on 6/30/14.
 */
public class ColorPalette {

    private static final Random RANDOM = new Random();
    private final Color[] colors;
    private int next = 0;

    public ColorPalette(Color... c) {        colors = c;    }

    public Color next() 			{        return colors[next++ % colors.length];    }
    public Color randomNext() 		{        return colors[RANDOM.nextInt(colors.length)];    }

}
