/**
   A maze is a 3-dimensional grid of tiles.  There are many different
   kinds of tiles; see Maze.Tile.

   @author Charlie Murphy
 */

import java.io.InputStream;
import java.util.Scanner;
import java.util.ArrayList;

class MazeParsingException extends Exception {
    public MazeParsingException() {}

    public MazeParsingException(String message) {
        super(message);
    }
}

class Maze {
    public enum Tile {
        EMPTY,
        WALL,
        PORTAL,
        STAIRCASE,
        PLAYER,
        FINISH
    };

    private int width;
    private int height;
    private int depth;
    private Tile[] tiles;
    
    public Maze(int width, int height, int depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        this.tiles  = new Tile[width * height * depth];
    }

    public Tile at(int x, int y, int z) {
        int index = z*width*height + y*width + x;
        return tiles[index];
    }

    public void setAt(int x, int y, int z, Tile tile) {
        int index = z*width*height + y*width + x;
        tiles[index] = tile;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    /**
       Loads a maze from an input stream.  If the parser finds that the map is invalid,
       it will throw an exception along with a helpful diagnosis.
     */
    public static Maze fromInputStream(InputStream stream) throws MazeParsingException {
        int w              = -1;
        int h              = -1;
        Scanner scan       = new Scanner(stream);
        int lineno         = 1;
        int rowcount       = 0; // how many rows are in the current maze level

        // We're gonna put tiles in this list.
        ArrayList<Tile> tilelist = new ArrayList<Tile>();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            // For each line of the file, there are 2 possibilities:
            //     1. The line is a separator (-------).
            //     2. The line represents a row of tiles.
            //
            // In the 2nd case, when the line is a row of tiles, is interesting.  There
            // are two special cases we have to handle:
            //
            // 2a. The row cannot be longer or shorter than the width of
            //     the maze.  The width of the maze is determined by the
            //     first row.
            //
            // 2b. For another thing, each level must have an amount of rows
            //     equal to the height of the maze.

            if (line.startsWith("-")) {
                // Only allow a separator once the width is known
                if (w < 0) {
                    throw new MazeParsingException("line " + lineno + ": separator can only happen after a row of tiles");
                }

                if (h < 0) {
                    h = rowcount;
                } else if (rowcount < h) {
                    throw new MazeParsingException("line " + lineno + ": level needs more rows");
                }

                rowcount = 0;
            } else {
                rowcount += 1;

                if (w < 0) {
                    w = line.length();
                } else if (h >= 0 && rowcount > h) {
                    throw new MazeParsingException("line " + lineno + ": level needs less rows");
                } else if (w < line.length()) {
                    throw new MazeParsingException("line " + lineno + ": row is too short");
                } else if (w > line.length()) {
                    throw new MazeParsingException("line " + lineno + ": row is too long");
                }

                // OK!  Seems to be a legal row of tiles.  Let's insert the tiles one by one.
                for (int i=0; i<line.length(); i++) {
                    switch (line.charAt(i)) {
                    case '.':
                        tilelist.add(Tile.EMPTY);
                        break;
                    case '#':
                        tilelist.add(Tile.WALL);
                        break;
                    case '=':
                        tilelist.add(Tile.PORTAL);
                        break;
                    case '+':
                        tilelist.add(Tile.STAIRCASE);
                        break;
                    case '@':
                        tilelist.add(Tile.PLAYER);
                        break;
                    case '*':
                        tilelist.add(Tile.FINISH);
                        break;
                    default:
                        throw new MazeParsingException("line " + lineno + ": unknown tile '" + line.charAt(i) + "'");
                    }
                }
            }

            lineno += 1;
        }

        // We finished reading the file.
        // But, the question is: did we get complete information?
        // If not, throw an exception.
        if (w < 0 || h < 0) {
            throw new MazeParsingException("input maze is corrupted");
        } else if (rowcount < h) {
            throw new MazeParsingException("the last level of the maze is too short");
        }

        // It's time to convert the info we got to a Maze object.
        // To do that, we need the depth (i.e. number of levels in the maze).
        int d = tilelist.size() / (w*h);

        // All right.
        Maze m = new Maze(w, h, d);

        // Paint m with the tiles in tilelist.
        for (int z=0; z<d; z++) {
            for (int y=0; y<h; y++) {
                for (int x=0; x<w; x++) {
                    m.setAt(x, y, z,
                            tilelist.get(z*w*h + y*w + x));
                }
            }
        }

        return m;
    }
}
