package cn.corbinhu.campusmarketing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author: Corbinhu
 * @description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    private int id;
    private String code;
    private String name;
    private String dataAccess;
}
