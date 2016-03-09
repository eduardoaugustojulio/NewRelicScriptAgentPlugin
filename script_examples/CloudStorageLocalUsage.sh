###############
############### DropBox local usage
###############

# dropBoxFolder -- UPDATE DROPBOX DIRECTORY LOCATION!!
dropBoxUsage=`du -shm ~/Dropbox`

db=`echo "${dropBoxUsage}" | awk '{ print $1 }'`
echo Metric:CloudStorageLocalUsage/DropBoxLocalUsage/${db}[Megabytes]


###############
############### Google Drive local usage
###############

# dropBoxFolder -- UPDATE GOOGLE DRIVE DIRECTORY LOCATION!!
googleDriveUsage=`du -shm ~/Google\ Drive/`

gd=`echo "${googleDriveUsage}" | awk '{ print $1 }'`
echo Metric:CloudStorageLocalUsage/GoogleDriveLocalUsage/${gd}[Megabytes]
