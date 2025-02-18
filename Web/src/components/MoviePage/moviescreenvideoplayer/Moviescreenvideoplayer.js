import React, { useCallback, useEffect, useRef, useState } from 'react';
import './Moviescreenvideoplayer.css';
import { useTheme } from '../../../ThemeContext';
import axios from 'axios';
import { useNavigate } from 'react-router';
import VideoRecommendations from './VideoRecommandations';
import VideoList from '../moviescreenvideolist/Moviescreenvideolist';


function VideoPlayer({ video, setSelectedVideo, setSidebarVideos, sidebarVideos }) {
 
  const [data, setData] = useState(video);
  const [author, setAuthor] = useState('');
  const [comment, setComment] = useState('');
  const [comments, setComments] = useState([]);
  const [likes, setLikes] = useState(0);
  const [dislikes, setDislikes] = useState(0);
  const [isLicked, setIsLicked] = useState(false);
  const [isDisliked, setIsDisliked] = useState(false);
  const [editingComment, setEditingComment] = useState(null);
  const [editText, setEditText] = useState('');
  const [isAuthor, setIsAuthor] = useState(false);
  const [showDialog, setShowDialog] = useState(false);
  const [newVideoName, setNewVideoName] = useState("");
  const [selectedRecommended, setSelectedRecommended] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [recommendationDetails, setRecommendationDetails] = useState([]);

  const navigate = useNavigate();

  const { theme, toggleTheme } = useTheme();

  let userData = sessionStorage.getItem('name');

  
  let lastVideoId = video._id;
  let haveSeen = false;


  useEffect(() => {

    console.table('use effect');


    const fetchData = async () => {
      let token = "";
      for (let i = 0; i < sessionStorage.length; i++) {
        const key = sessionStorage.key(i);
        if (key === 'token') {
          token = sessionStorage.getItem(key);
        }
      }
  
      try {
        const addView = await axios.put(`http://localhost:5002/api/videos/${video._id}/addView`, {}, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const response = await axios.get(`http://localhost:5002/api/videos/${video._id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
       
        console.log(response);
        setRecommendations(response.data.recommendations || []); // Set recommendations here
        setData(response.data.video);
        setComments(response.data.video.comments || []);
        setLikes(response.data.video.likes || 0);
        setDislikes(response.data.video.dislikes || 0);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    if(!haveSeen) {
      fetchData();
      haveSeen = true;
    }
  }, [video._id]);
  


  

  useEffect(() => {
    if (data && data.comments && Array.isArray(data.comments)) {
      setComments(data.comments);
    } else {
      setComments([]);
    }

    if (data && data.likes) {
      setLikes(data.likes);
    } else {
      setLikes(0);
    }
    if (data && data.dislikes) {
      setDislikes(data.dislikes);
    } else {
      setDislikes(0);
    }

    console.log("user data: " + userData);

    if (data && data.channel === userData) {
      setIsAuthor(true);
    } else {
      console.log(isAuthor + " author");
    }
  }, [data, userData]);

  useEffect(() => {
    const fetchRecommendationDetails = async () => {
      try {
        const detailsPromises = recommendations.map(id =>
          axios.get(`http://localhost:5002/api/videos/${id}`)
        );
        const detailsResponses = await Promise.all(detailsPromises);
        const details = detailsResponses.map(response => response.data.video);
        setRecommendationDetails(details.filter(recVideo => recVideo._id !== data._id));

        
      // Pass recommendations to sidebar
        if(details.length < 6) {
          
          console.log(sidebarVideos)
          const arr = [...sidebarVideos.filter(vid => vid._id !== data._id), ...details.filter(recVideo => recVideo._id !== data._id)];
          const uniqueArr = arr.filter((video, index, self) =>
            index === self.findIndex((v) => v._id === video._id)
          );
          setSidebarVideos(uniqueArr);
          console.log(uniqueArr)
        } else {
          console.log("more than 6");
          setSidebarVideos(details.filter(recVideo => recVideo._id !== data._id));
        }
      } catch (error) {
        console.error('Error fetching recommendation details:', error);
      }
    };
  
    if (recommendations.length > 0) {
      fetchRecommendationDetails();
    }
  }, [recommendations, data._id, setSidebarVideos]);


  const handleDownload = (e) => {
    e.preventDefault();
    const link = document.createElement('a');
    link.href = data.path;
    link.download = data.title;
    link.click();
  };

  const handleLiked = async (e) => {
    e.preventDefault();
    try {
      if (isLicked) {
        // Unlike the video
        const response = await axios.post(`http://localhost:5002/api/videos/${data._id}/unlike`, {
          videoId: data.id
        });
        if (response.status === 200) {
          setLikes(likes - 1);
          setIsLicked(false);
        }
      } else {
        // Like the video
        const response = await axios.post(`http://localhost:5002/api/videos/${data._id}/like`, {
          videoId: data.id
        });
        if (response.status === 200) {
          setLikes(likes + 1);
          setIsLicked(true);

          if (isDisliked) {
            setDislikes(dislikes - 1);
            setIsDisliked(false);
          }
        }
      }
    } catch (error) {
      console.error('Error handling like:', error);
    }
  };

  

  const handleDisliked = async (e) => {
    e.preventDefault();
    try {
      if (isDisliked) {
        // Remove dislike
        const response = await axios.post(`http://localhost:5002/api/videos/${video._id}/undislike`, {
          videoId: video._id
          
        });
        if (response.status === 200) {
          setDislikes(dislikes - 1);
          setIsDisliked(false);
        }
      } else {
        // Dislike the video
        const response = await axios.post(`http://localhost:5002/api/videos/${video._id}/dislike`, {
         
          
        });
        if (response.status === 200) {
          setDislikes(dislikes + 1);
          setIsDisliked(true);
  
          if (isLicked) {
            setLikes(likes - 1);
            setIsLicked(false);
          }
        }
      }
    } catch (error) {
      console.error('Error handling dislike:', error);
    }
  };

  const username = sessionStorage.getItem('name');


  const addComment = async () => {
    const name = sessionStorage.getItem('name');
    if (!name || name === '') {
      navigate('/login');
      return;
    }
    if(!comment || comment === '') {
        alert("please enter a comment");
        return;
    }
    try {
      const response = await fetch(`http://localhost:5002/api/videos/${video._id}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ author: name, comment }),
      });
      if (response.ok) {
        const newComment = {  author: name, comment };
        setComments([...comments, newComment]);
        setComment(''); 
      } else {
        throw new Error('Failed to add comment.');
      }
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const deleteComment = async (videoId, commentText, author) => {
    try {
      const response = await axios.delete(`http://localhost:5002/api/videos/${videoId}/comments`, {
        data: {
          commentText,
          author
        }
      });
  
      if (response.data.success) {
        setComments(comments.filter(comment => comment.comment !== commentText || comment.author !== author));
      } else {
        console.error('Error deleting comment:', response.data.message);
      }
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const editComment = async (videoId, author, originalComment, newCommentText) => {
    try {
      const response = await axios.put(`http://localhost:5002/api/videos/${videoId}/comments`, {
        author,
        originalComment,
        newCommentText
      });
  
      if (response.data.success) {
        setComments(
          comments.map(comment =>
            (comment.comment === originalComment && comment.author === author)
              ? { ...comment, comment: newCommentText }
              : comment
          )
        );
        setEditingComment(null);
        setEditText('');
      } else {
        console.error('Error editing comment:', response.data.message);
      }
    } catch (error) {
      console.error('Error editing comment:', error);
    }
  };

  const handleDelete = async() => {
    const response = await axios.delete(`http://localhost:5002/api/videos/${video._id}`);
    if(response.data.success) {
      navigate('/');
    } else {
      alert("error deleting video");
    }
  }


  const handleDeleteComment = (commentText, author) => {
    deleteComment(video._id, commentText, author);
  };
  const handleEditComment = (author, originalComment, newCommentText) => {
    editComment(video._id, author, originalComment, newCommentText);
  };

  const handleEdit = () => {
    setNewVideoName(video.title);
    setShowDialog(true);


  }
  const handleSubmit = async() => {
    try {
      const response = await axios.put(`http://localhost:5002/api/videos/${video._id}`, {
        newName: newVideoName
      });
  
      if (response.data.success) {
        video.title = newVideoName; // Update the video title locally
        setShowDialog(false);
        // Optionally, you can update the state or trigger a re-render here if needed
        console.log('Video title updated successfully:', response.data.video);
      } else {
        console.error('Error updating video title:', response.data.message);
      }
    } catch (error) {
      console.error('Error updating video title:', error.response.data.message);
    }
  

  }

  const handleSelectRecommended = (video) => {
    setSelectedVideo(video);
  };


  const shareVideo = async () => {
    try {
      await navigator.share({
        title: video.title,
        url: video.path,
      });
      console.log('Successfully shared');
    } catch (error) {
      console.error('Error sharing:', error);
    }
  };

  const videoSrc = video.path && video.path.includes("/videos") ? video.path : `http://localhost:5002/${video.path}`;
  const recommendedVideos = recommendationDetails.filter(
    (recVideo, index, self) =>
      recVideo._id !== data._id && self.findIndex(v => v._id === recVideo._id) === index
  );


  return (
    <div className={`video-container ${theme}`}>
      <div className="video-player">
        {data  ? (
          <div>
          <div className="video-container2">
            <video key={video.path} controls>
              <source src={videoSrc} type="video/mp4" />
              Your browser does not support the video tag.
            </video>
          </div>
         
            <h2 className="details-item">{video.title}</h2>
            <div className="details-bar">
              <h3 className="details-item">views: {data.views}</h3>
              <h3 className="details-item">channel: {data.channel}</h3>

              <svg onClick={handleLiked} width="30px" height="30px" viewBox="3 4 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 10V20M8 10L4 9.99998V20L8 20M8 10L13.1956 3.93847C13.6886 3.3633 14.4642 3.11604 15.1992 3.29977L15.2467 3.31166C16.5885 3.64711 17.1929 5.21057 16.4258 6.36135L14 9.99998H18.5604C19.8225 9.99998 20.7691 11.1546 20.5216 12.3922L19.3216 18.3922C19.1346 19.3271 18.3138 20 17.3604 20L8 20" stroke="#000000" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
              <h3 className="details-item">{likes}</h3>

              <svg onClick={handleDisliked} width="30px" height="30px" viewBox="3 4 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 14V4M8 14L4 14V4.00002L8 4M8 14L13.1956 20.0615C13.6886 20.6367 14.4642 20.884 15.1992 20.7002L15.2467 20.6883C16.5885 20.3529 17.1929 18.7894 16.4258 17.6387L14 14H18.5604C19.8225 14 20.7691 12.8454 20.5216 11.6078L19.3216 5.60779C19.1346 4.67294 18.3138 4.00002 17.3604 4.00002L8 4" stroke="#000000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              </svg>

              <svg onClick={shareVideo} width="30px" height="30px" viewBox="-0.5 0 25 25" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M13.47 4.13998C12.74 4.35998 12.28 5.96 12.09 7.91C6.77997 7.91 2 13.4802 2 20.0802C4.19 14.0802 8.99995 12.45 12.14 12.45C12.34 14.21 12.79 15.6202 13.47 15.8202C15.57 16.4302 22 12.4401 22 9.98006C22 7.52006 15.57 3.52998 13.47 4.13998Z" stroke="#000000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
              <svg onClick={handleDownload} width="30px" height="30px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 20.8201C15.426 22.392 8.574 22.392 2 20.8201" stroke="#000000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                <path d="M11.9492 2V16" stroke="#000000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                <path d="M16.8996 11.8L13.3796 15.4099C13.2011 15.5978 12.9863 15.7476 12.7482 15.8499C12.5101 15.9521 12.2538 16.0046 11.9946 16.0046C11.7355 16.0046 11.4791 15.9521 11.241 15.8499C11.0029 15.7476 10.7881 15.5978 10.6096 15.4099L7.09961 11.8" stroke="#000000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
              {isAuthor && (
                <>
                  <svg onClick={handleEdit} width="30px" height="30px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">


                    <path d="M9.65661 17L6.99975 17L6.99975 14M6.10235 14.8974L17.4107 3.58902C18.1918 2.80797 19.4581 2.80797 20.2392 3.58902C21.0202 4.37007 21.0202 5.6364 20.2392 6.41745L8.764 17.8926C8.22794 18.4287 7.95992 18.6967 7.6632 18.9271C7.39965 19.1318 7.11947 19.3142 6.8256 19.4723C6.49475 19.6503 6.14115 19.7868 5.43395 20.0599L3 20.9998L3.78312 18.6501C4.05039 17.8483 4.18403 17.4473 4.3699 17.0729C4.53497 16.7404 4.73054 16.424 4.95409 16.1276C5.20582 15.7939 5.50466 15.4951 6.10235 14.8974Z" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                  <svg onClick={handleDelete} width="30px" height="30px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M10 12L14 16M14 12L10 16M4 6H20M16 6L15.7294 5.18807C15.4671 4.40125 15.3359 4.00784 15.0927 3.71698C14.8779 3.46013 14.6021 3.26132 14.2905 3.13878C13.9376 3 13.523 3 12.6936 3H11.3064C10.477 3 10.0624 3 9.70951 3.13878C9.39792 3.26132 9.12208 3.46013 8.90729 3.71698C8.66405 4.00784 8.53292 4.40125 8.27064 5.18807L8 6M18 6V16.2C18 17.8802 18 18.7202 17.673 19.362C17.3854 19.9265 16.9265 20.3854 16.362 20.673C15.7202 21 14.8802 21 13.2 21H10.8C9.11984 21 8.27976 21 7.63803 20.673C7.07354 20.3854 6.6146 19.9265 6.32698 19.362C6 18.7202 6 17.8802 6 16.2V6" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                </>
              )}
              {showDialog && (
                <div className="popup">
                  <div className="popup-inner">
                    <h2>Edit Video Name</h2>
                    <input type="text" value={newVideoName} onChange={(e) => setNewVideoName(e.target.value)} />
                    <button onClick={handleSubmit}>Submit</button>

                  </div>
                </div>
              )}
            </div>


            
               
                

            <input className='comment-input' type='text' placeholder='Add a comment...' value={comment} onChange={(e) => setComment(e.target.value)} />
            <button className='Btn' onClick={addComment}> Add Comment </button>

            {comments.map(comment => (
              <div className='comment' key={`${comment.author}-${comment.comment}`}>
                <div className="content">
                  {editingComment === `${comment.author}-${comment.comment}` ? (
                    <>
                      <input
                        type="text"
                        value={editText}
                        onChange={(e) => setEditText(e.target.value)}
                      />
                      <button className='edit-btn' onClick={() => handleEditComment(comment.author, comment.comment, editText)}>Save</button>
                      <button className='delete-btn' onClick={() => setEditingComment(null)}>Cancel</button>
                    </>
                  ) : (
                    <>
                      <div>
                        <strong>{typeof comment === 'string' ? 'guest' : comment.author || 'guest'}:</strong>
                        {typeof comment === 'string' ? comment : comment.comment}
                      </div>
                      {username === (typeof comment === 'string' ? 'guest' : comment.author) && (
                        <div className="actions">
                          <button className='edit-btn' onClick={() => {
                            setEditingComment(`${comment.author}-${comment.comment}`);
                            setEditText(typeof comment === 'string' ? comment : comment.comment);
                          }}>Edit</button>
                          <button className='delete-btn' onClick={() => handleDeleteComment(comment.comment, comment.author)}>Delete</button>
                        </div>
                      )}
                    </>
                  )}
                </div>
              </div>
            ))}
              <div>
       




    </div>

          </div>
        ) : (
          <div>Loading...</div>
        )}
      </div>
    </div>
  );
}

export default VideoPlayer;