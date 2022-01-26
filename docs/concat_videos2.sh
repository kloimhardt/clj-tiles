ffmpeg -i MAVA.mov -i M3.m4v -filter_complex "[0:v:0][0:a:0][1:v:0][1:a:0]concat=n=2:v=1:a=1[outv][outa]" \
-map "[outv]" -map "[outa]" output.mp4
