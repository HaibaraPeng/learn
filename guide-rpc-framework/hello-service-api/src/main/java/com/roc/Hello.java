package com.roc;

import lombok.*;

import java.io.Serializable;

/**
 * @Description Hello
 * @Author penn
 * @Date 2022/7/31 10:22
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private String description;
}
