import React, { useEffect, useState } from 'react';
import VideoItem from '../moviescreenvideoitem/Moviescreenvideoitem';
import data from '../../../data/data.json';
import './Moviescreenvideolist.css';
import { useLocation } from 'react-router-dom';

const VideoList = ({ key, setSelectedVideo, allVideos }) => {
    const [videos, setVideos] = useState([]);
    const location = useLocation();

    useEffect(() => {
        if(allVideos) {
            // Filter and sort videos by views in descending order
            const filteredVideos = allVideos
                .filter(video => video.views > 0)
                .sort((a, b) => b.views - a.views); // Sort by views
            setVideos(filteredVideos);
           }  else {
        setVideos(data);
       }
    }, [allVideos]);

    

    return (
        <div>
            <section className="MS-video-list">
                {videos ? (
                    videos.map((video, index) => (
                        <VideoItem key={video.id} video={video} setSelectedVideo={setSelectedVideo} />
                    ))
                ) : (
                    <div>Loading...</div>
                )}
            </section>
        </div>
    );
};

export default VideoList;