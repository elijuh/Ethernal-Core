package me.elijuh.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<X, Y> {
    private X x;
    private Y y;
}
