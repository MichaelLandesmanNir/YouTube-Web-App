import React from 'react';
import VideoList from '../moviescreenvideolist/Moviescreenvideolist';

export default function VideoRecommendations({ recommendationDetails, handleSelectRecommended, video }) {
  return (
    <div>
      {video ? (
        <div className="recommendations">
          <h3>Recommended Videos</h3>
          {recommendationDetails && recommendationDetails.length > 0 ? (
            <VideoList 
              setSelectedVideo={handleSelectRecommended} 
              allVideos={recommendationDetails.filter((recVideo, index, self) =>
                index === self.findIndex(v => v.title === recVideo.title && v._id === recVideo._id) &&
                recVideo._id !== video._id // Filter out the current video
              )}   
            />
          ) : (
            <p>No recommendations available</p>
          )}
        </div>
      ) : (
        <div>Loading...</div>
      )}
    </div>
  );
}
