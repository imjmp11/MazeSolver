import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MazeTest {
    public static void main(String[] args) throws FileNotFoundException, IOException, MazeParsingException {
        // Load our maze
        FileInputStream fis = new FileInputStream("maze.txt");
        Maze m = Maze.fromInputStream(fis);
        fis.close();

        // Now print all the tiles to the console
        int x, y, z;
        int depth  = m.getDepth();
        int height = m.getHeight();
        int width  = m.getWidth();

        for (z = 0; z < depth; z++) {
            System.out.println("Level " + z);

            for (y = 0; y < height; y++) {
                System.out.print("    ");

                for (x = 0; x < width; x++) {
                    Maze.Tile t = m.at(x, y, z);

                    switch (t) {
                    case EMPTY:
                        System.out.print(".");
                        break;
                    case WALL:
                        System.out.print("#");
                        break;
                    case PORTAL:
                        System.out.print("=");
                        break;
                    case STAIRCASE:
                        System.out.print("+");
                        break;
                    case PLAYER:
                        System.out.print("@");
                        break;
                    case FINISH:
                        System.out.print("*");
                        break;
                    }
                }

                System.out.println();
            }
        }
    }
}
