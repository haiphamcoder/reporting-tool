package com.haiphamcoder.usermanagement.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pair<T, U> {
    private T first;
    private U second;
}
