#!/bin/bash

# Base directory
BASE_DIR="userprofile/src/main/java/com/tripbook/userprofileManfoIngrid"

# Function to create directory if it doesn't exist
create_dir() {
    if [ ! -d "$1" ]; then
        mkdir -p "$1"
        echo "Created directory: $1"
    else
        echo "Directory already exists: $1"
    fi
}

# Function to create file if it doesn't exist
create_file() {
    if [ ! -f "$1" ]; then
        touch "$1"
        echo "Created file: $1"
    else
        echo "File already exists: $1"
    fi
}

echo "Creating folder structure for TripBook UserProfile module..."

# Create data layer directories
create_dir "$BASE_DIR/data/local/dao"
create_dir "$BASE_DIR/data/local/database"
create_dir "$BASE_DIR/data/local/entities"
create_dir "$BASE_DIR/data/remote/api"
create_dir "$BASE_DIR/data/remote/dto"
create_dir "$BASE_DIR/data/repository"

# Create domain layer directories
create_dir "$BASE_DIR/domain/model"
create_dir "$BASE_DIR/domain/repository"
create_dir "$BASE_DIR/domain/usecase"

# Create presentation layer directories
create_dir "$BASE_DIR/presentation/media/components/dialogs"
create_dir "$BASE_DIR/presentation/media/components/items"
create_dir "$BASE_DIR/presentation/media/components/grid"
create_dir "$BASE_DIR/presentation/media/viewmodel"
create_dir "$BASE_DIR/presentation/media/screen"
create_dir "$BASE_DIR/presentation/theme"
create_dir "$BASE_DIR/presentation/utils"

# Create DI directory
create_dir "$BASE_DIR/di"

# Create data layer files
create_file "$BASE_DIR/data/local/dao/MediaDao.kt"
create_file "$BASE_DIR/data/local/database/MediaDatabase.kt"
create_file "$BASE_DIR/data/local/entities/MediaEntity.kt"
create_file "$BASE_DIR/data/remote/api/MediaApi.kt"
create_file "$BASE_DIR/data/remote/dto/MediaDto.kt"
create_file "$BASE_DIR/data/repository/MediaRepositoryImpl.kt"

# Create domain layer files
create_file "$BASE_DIR/domain/model/MediaItem.kt"
create_file "$BASE_DIR/domain/repository/MediaRepository.kt"
create_file "$BASE_DIR/domain/usecase/DeleteMediaUseCase.kt"
create_file "$BASE_DIR/domain/usecase/GetMediaListUseCase.kt"
create_file "$BASE_DIR/domain/usecase/UpdateMediaUseCase.kt"
create_file "$BASE_DIR/domain/usecase/ShareMediaUseCase.kt"

# Create presentation layer files
create_file "$BASE_DIR/presentation/media/components/dialogs/EditMediaDialog.kt"
create_file "$BASE_DIR/presentation/media/components/dialogs/MediaActionDialog.kt"
create_file "$BASE_DIR/presentation/media/components/dialogs/ShareDialog.kt"
create_file "$BASE_DIR/presentation/media/components/items/FilterTab.kt"
create_file "$BASE_DIR/presentation/media/components/items/MediaItemCard.kt"
create_file "$BASE_DIR/presentation/media/components/items/ProfileHeader.kt"
create_file "$BASE_DIR/presentation/media/components/grid/MediaGrid.kt"
create_file "$BASE_DIR/presentation/media/viewmodel/MediaViewModel.kt"
create_file "$BASE_DIR/presentation/media/screen/MediaOrganizationScreen.kt"
create_file "$BASE_DIR/presentation/theme/Color.kt"
create_file "$BASE_DIR/presentation/theme/Theme.kt"
create_file "$BASE_DIR/presentation/theme/Type.kt"
create_file "$BASE_DIR/presentation/utils/MediaUtils.kt"
create_file "$BASE_DIR/presentation/utils/ShareUtils.kt"

# Create DI files
create_file "$BASE_DIR/di/MediaModule.kt"

echo "Folder structure creation completed!"
echo "Total directories created: $(find $BASE_DIR -type d | wc -l)"
echo "Total files created: $(find $BASE_DIR -type f | wc -l)"
