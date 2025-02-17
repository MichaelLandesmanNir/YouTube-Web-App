import React from 'react';
import './Moviescreenvideoitem.css';
import VideoPlayer from '../moviescreenvideoplayer/Moviescreenvideoplayer';

const VideoItem = ({  video, setSelectedVideo }) => {

    const handleOnClick = (video) => {
        console.log("video is Ptressed!!!: ", video.path)
        setSelectedVideo(video);
    }


    const imgSrc = `http://localhost:5002/${video.image}`;
    console.log(imgSrc)
    return (
        <div className="MS-video-item">
            <img src={imgSrc} alt={video.title} />
            <div className="MS-video-info" onClick= {() => handleOnClick(video)}>
                <h3>{video.title}</h3>
                <p>{video.channel} • {video.views} views • {video.time}</p>
            </div>
        </div>
    );
};

export default VideoItem;
