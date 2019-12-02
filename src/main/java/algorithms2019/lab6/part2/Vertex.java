package algorithms2019.lab6.part2;

import lombok.Getter;
import lombok.Setter;

/*
ideally cost should be set in Edge(from, to, weight) to have different weights
for different vertices-predecessors, but in this task we assume, that paths
between neighbour squares are all equal and set to 1. If this value is set to null,
it means there is no way to get into this vertex - it is a barrier.
 */
@Getter
public class Vertex {
    private int row; //horizontal position on grid
    private int column; //vertical position on grid
    private Integer cost; // cost to reach the vertex

    @Setter
    private int g; //distance form source to this vertex
    @Setter
    private double f; // heuristic value of function "distance + cost"

    public Vertex (int row, int column, Integer cost){
        this.row = row;
        this.column = column;
        this.cost = cost;
    }
}
