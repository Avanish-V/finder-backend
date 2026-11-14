package com.iotabuild.campuscircle.UserService.Repository

import com.iotabuild.campuscircle.UserService.Models.Entity.DeviceToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface DeviceTokenRepository : JpaRepository<DeviceToken, Long> {

    /** Lookup by raw FCM token string (globally unique due to db constraint). */
    fun findByFcmToken(fcmToken: String): DeviceToken?

    /** All active tokens for a given user UID. */
    fun findAllByUserUidAndIsActiveTrue(userUid: String): List<DeviceToken>

    /** All tokens (active + inactive) for a user — used on logout to deactivate all. */
    fun findAllByUserUid(userUid: String): List<DeviceToken>

    /** Bulk-deactivate all tokens for a user on logout. */
    @Modifying
    @Query("UPDATE DeviceToken d SET d.isActive = false WHERE d.user.uid = :uid")
    fun deactivateAllByUserUid(uid: String): Int

    /** Clear an expired/revoked token by its FCM string. */
    @Modifying
    @Query("DELETE FROM DeviceToken d WHERE d.fcmToken = :fcmToken")
    fun deleteByFcmToken(fcmToken: String): Int

    /** Purge stale inactive tokens older than a given cutoff (for scheduled cleanup). */
    @Modifying
    @Query("DELETE FROM DeviceToken d WHERE d.isActive = false AND d.lastSeenAt < :cutoff")
    fun deleteStaleInactive(cutoff: Instant): Int
}
