#!/bin/bash

# Base directory to remove
BASE_DIR="userprofile/src/main/java/com/tripbook/userprofileManfoIngrid"

# Function to remove directory if it exists
remove_dir() {
    if [ -d "$1" ]; then
        rm -rf "$1"
        echo "Removed directory: $1"
    else
        echo "Directory does not exist: $1"
    fi
}

# Function to remove file if it exists
remove_file() {
    if [ -f "$1" ]; then
        rm -f "$1"
        echo "Removed file: $1"
    else
        echo "File does not exist: $1"
    fi
}

echo "Removing TripBook UserProfile module structure..."

# Remove individual files first (optional, since removing parent dirs will remove them)
echo "Removing files..."

# Data layer files
remove_file "$BASE_DIR/data/local/dao/MediaDao.kt"
remove_file "$BASE_DIR/data/local/database/MediaDatabase.kt"
remove_file "$BASE_DIR/data/local/entities/MediaEntity.kt"
remove_file "$BASE_DIR/data/remote/api/MediaApi.kt"
remove_file "$BASE_DIR/data/remote/dto/MediaDto.kt"
remove_file "$BASE_DIR/data/repository/MediaRepositoryImpl.kt"

# Domain layer files
remove_file "$BASE_DIR/domain/model/MediaItem.kt"
remove_file "$BASE_DIR/domain/repository/MediaRepository.kt"
remove_file "$BASE_DIR/domain/usecase/DeleteMediaUseCase.kt"
remove_file "$BASE_DIR/domain/usecase/GetMediaListUseCase.kt"
remove_file "$BASE_DIR/domain/usecase/UpdateMediaUseCase.kt"
remove_file "$BASE_DIR/domain/usecase/ShareMediaUseCase.kt"

# Presentation layer files
remove_file "$BASE_DIR/presentation/media/components/dialogs/EditMediaDialog.kt"
remove_file "$BASE_DIR/presentation/media/components/dialogs/MediaActionDialog.kt"
remove_file "$BASE_DIR/presentation/media/components/dialogs/ShareDialog.kt"
remove_file "$BASE_DIR/presentation/media/components/items/FilterTab.kt"
remove_file "$BASE_DIR/presentation/media/components/items/MediaItemCard.kt"
remove_file "$BASE_DIR/presentation/media/components/items/ProfileHeader.kt"
remove_file "$BASE_DIR/presentation/media/components/grid/MediaGrid.kt"
remove_file "$BASE_DIR/presentation/media/viewmodel/MediaViewModel.kt"
remove_file "$BASE_DIR/presentation/media/screen/MediaOrganizationScreen.kt"
remove_file "$BASE_DIR/presentation/theme/Color.kt"
remove_file "$BASE_DIR/presentation/theme/Theme.kt"
remove_file "$BASE_DIR/presentation/theme/Type.kt"
remove_file "$BASE_DIR/presentation/utils/MediaUtils.kt"
remove_file "$BASE_DIR/presentation/utils/ShareUtils.kt"

# DI files
remove_file "$BASE_DIR/di/MediaModule.kt"

# Remove the existing Mediaorganisation.kt file
remove_file "$BASE_DIR/presentation/media/Mediaorganisation.kt"

echo "Removing directories..."

# Remove directories (from deepest to shallowest)
remove_dir "$BASE_DIR/data/local/dao"
remove_dir "$BASE_DIR/data/local/database"
remove_dir "$BASE_DIR/data/local/entities"
remove_dir "$BASE_DIR/data/local"
remove_dir "$BASE_DIR/data/remote/api"
remove_dir "$BASE_DIR/data/remote/dto"
remove_dir "$BASE_DIR/data/remote"
remove_dir "$BASE_DIR/data/repository"
remove_dir "$BASE_DIR/data"

remove_dir "$BASE_DIR/domain/model"
remove_dir "$BASE_DIR/domain/repository"
remove_dir "$BASE_DIR/domain/usecase"
remove_dir "$BASE_DIR/domain"

remove_dir "$BASE_DIR/presentation/media/components/dialogs"
remove_dir "$BASE_DIR/presentation/media/components/items"
remove_dir "$BASE_DIR/presentation/media/components/grid"
remove_dir "$BASE_DIR/presentation/media/components"
remove_dir "$BASE_DIR/presentation/media/viewmodel"
remove_dir "$BASE_DIR/presentation/media/screen"
remove_dir "$BASE_DIR/presentation/media"
remove_dir "$BASE_DIR/presentation/theme"
remove_dir "$BASE_DIR/presentation/utils"
remove_dir "$BASE_DIR/presentation"

remove_dir "$BASE_DIR/di"

# Finally remove the base directory if it's empty
if [ -d "$BASE_DIR" ] && [ -z "$(ls -A "$BASE_DIR")" ]; then
    remove_dir "$BASE_DIR"
    echo "Removed empty base directory: $BASE_DIR"
elif [ -d "$BASE_DIR" ]; then
    echo "Base directory not empty, keeping: $BASE_DIR"
    echo "Remaining contents:"
    ls -la "$BASE_DIR"
fi

echo "Cleanup completed!"

# Optional: Remove parent directories if they become empty
echo "Checking for empty parent directories..."
PARENT_DIR="userprofile/src/main/java/com/tripbook"
if [ -d "$PARENT_DIR" ] && [ -z "$(ls -A "$PARENT_DIR")" ]; then
    remove_dir "$PARENT_DIR"
fi

PARENT_DIR="userprofile/src/main/java/com"
if [ -d "$PARENT_DIR" ] && [ -z "$(ls -A "$PARENT_DIR")" ]; then
    remove_dir "$PARENT_DIR"
fi

PARENT_DIR="userprofile/src/main/java"
if [ -d "$PARENT_DIR" ] && [ -z "$(ls -A "$PARENT_DIR")" ]; then
    remove_dir "$PARENT_DIR"
fi

PARENT_DIR="userprofile/src/main"
if [ -d "$PARENT_DIR" ] && [ -z "$(ls -A "$PARENT_DIR")" ]; then
    remove_dir "$PARENT_DIR"
fi

PARENT_DIR="userprofile/src"
if [ -d "$PARENT_DIR" ] && [ -z "$(ls -A "$PARENT_DIR")" ]; then
    remove_dir "$PARENT_DIR"
fi

PARENT_DIR="userprofile"
if [ -d "$PARENT_DIR" ] && [ -z "$(ls -A "$PARENT_DIR")" ]; then
    remove_dir "$PARENT_DIR"
fi

echo "Full cleanup completed!"
