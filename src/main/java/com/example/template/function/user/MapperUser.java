package com.example.template.function.user;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MapperUser {
    AhaUser.Gotten getUser(AhaUser.Getting getting);
    int createUser(AhaUser.Creating creating);
    int putUser(AhaUser.Putting putting);
    int patchUser(AhaUser.Patching patching);
    int removeUser(AhaUser.Removing removing);
    AhaUserImage.Downloaded getFileDownload(AhaUserImage.Downloading downloading);
    int createUserImage(AhaUserImage.Uploading uploading);
}
