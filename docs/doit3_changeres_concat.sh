# ffmpeg -i MAVApresentation.m4v -s 1440x830 M3.m4v
#ffmpeg -i MAVApresentation.m4v -vf "scale=w=1440:h=830:force_original_aspect_ratio=1,pad=1440:830:(ow-iw)/2:(oh-ih)/2" M3.m4v 
#ffmpeg -i MAVApresentation.m4v -vf "scale=w=1440:h=830:force_original_aspect_ratio=2,crop=1440:830" M3.m4v 

ffmpeg -i MAVApresentation.m4v -vf "scale=-1:830,pad=1440:ih:(ow-iw)/2" MAVApresentation_rescale.m4v 
ffmpeg -f concat -i concat_files3.txt -c copy MAVA_final2_compress.mp4
