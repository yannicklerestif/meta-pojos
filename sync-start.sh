lftp -f ../lftp-script
while inotifywait -r -e modify,attrib,close_write,moved_to,moved_from,move_self,create,delete,delete_self,unmount \
./meta-pojos-documentation; do lftp -f ../lftp-script; done

