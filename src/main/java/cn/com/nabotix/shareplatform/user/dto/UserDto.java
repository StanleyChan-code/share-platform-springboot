package cn.com.nabotix.shareplatform.user.dto;

import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.Data;

import java.util.UUID;

/**
 * 数据集监督者DTO
 * 用于向公众展示数据集监督者的基本信息
 *
 * @author 陈雍文
 */
@Data
public class UserDto {
    private UUID id;
    private String username;
    private String realName;
    private String title;

    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setRealName(user.getRealName());
        userDto.setTitle(user.getTitle());
        return userDto;
    }
}
