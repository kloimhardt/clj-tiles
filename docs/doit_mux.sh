# ffmpeg -i VAall.mov -vcodec copy -an VAall_nosound.mov
# ffmpeg -i MAall.m4a -i VAall_nosound.mov -filter_complex adelay=1000^|1000 MAVA.mpg
# ffmpeg -i MAall.m4a -i VAall_nosound.mov -c copy MAVA.mpg
# ffmpeg -i MAall.m4a -i VAall_nosound.mov -c copy -map 1:v:0 -map ?1:a:0 MAVA.mov
# ffmpeg -i MAall.m4a -i VAall_nosound.mov -c copy -map 1:v:0 -map 0:a:0 MAVA.mov
ffmpeg -i MAall.m4a -i VAall.mov -c copy -map 1:v:0 -map 0:a:0 MAVA.mov
ffmpeg -i MAall.m4a -i VAall_compress.mp4 -c copy -map 1:v:0 -map 0:a:0 MAVA_compress.mp4
