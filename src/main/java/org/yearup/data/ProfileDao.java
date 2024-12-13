package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile getById(int userId);
    Profile create(Profile profile);
    void update(int userId, Profile profile);
}
