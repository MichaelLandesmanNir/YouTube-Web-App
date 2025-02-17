const { verifyAuthToken } = require('../utils/authService');


const auth = (req, res, next) => {
  const authHeader = req.header('Authorization');
  
  if (!authHeader) {
    return res.status(401).json({ message: "no token" });
  }

  const [bearer, token] = authHeader.split(' ');

  if (bearer !== 'Bearer' || !token) {
    return res.status(401).json({ message: "invalid auth format"});
  }

  try {
    const userId = verifyAuthToken(token);
    req.user = { id: userId };
    next();
  } catch (error) {
    console.error('Authentication error:', error);
    res.status(401).json({ message: "invalid token" });
  }
};

module.exports = auth;

