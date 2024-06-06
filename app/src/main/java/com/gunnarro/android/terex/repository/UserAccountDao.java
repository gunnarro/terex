package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.UserAccount;

@Dao
public interface UserAccountDao {

    @Query("SELECT * FROM user_account a WHERE a.is_default_user = 1")
    UserAccount getDefaultUserAccount();

    @Query("SELECT * FROM user_account a WHERE a.id = :userAccountId")
    UserAccount getUserAccount(Long userAccountId);

    @Query("SELECT * FROM user_account a WHERE a.user_name = :userName")
    UserAccount findUserAccount(String userName);

    /**
     * @param userAccount invoice file to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(UserAccount userAccount);

    /**
     * @param userAccount updated invoice file. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(UserAccount userAccount);

    /**
     * @param userAccount to be deleted
     */
    @Delete
    void delete(UserAccount userAccount);
}
