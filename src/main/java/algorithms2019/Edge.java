package algorithms2019;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Edge {
    private Integer from;
    private Integer to;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof Edge) {
            Edge edge = (Edge) o;
            return from != null && to != null ? (from.equals(edge.from)
                    && to.equals(edge.to)) || (from.equals(edge.to)
                    && to.equals(edge.from)) : to == null && from == null;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return from.hashCode() + to.hashCode();
    }

}
