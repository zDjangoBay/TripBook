package com.android.tripbook.data.local.mapper

import com.android.tripbook.data.local.entity.TravelAgencyEntity
import com.android.tripbook.data.model.TravelAgency

/**
 * Mapper class to convert between TravelAgency domain model and TravelAgencyEntity Room entity
 */
object TravelAgencyMapper {
    /**
     * Convert from domain model to entity
     */
    fun toEntity(agency: TravelAgency): TravelAgencyEntity {
        return TravelAgencyEntity(
            id = agency.id,
            name = agency.name,
            description = agency.description,
            logo = agency.logo,
            website = agency.website,
            contactPhone = agency.contactPhone,
            contactEmail = agency.contactEmail,
            createdAt = agency.createdAt,
            updatedAt = agency.updatedAt
        )
    }

    /**
     * Convert from entity to domain model
     */
    fun fromEntity(entity: TravelAgencyEntity): TravelAgency {
        return TravelAgency(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            logo = entity.logo,
            website = entity.website,
            contactPhone = entity.contactPhone,
            contactEmail = entity.contactEmail,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert a list of entities to domain models
     */
    fun fromEntities(entities: List<TravelAgencyEntity>): List<TravelAgency> {
        return entities.map { fromEntity(it) }
    }
}
