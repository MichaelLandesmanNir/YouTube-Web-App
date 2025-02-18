const jwt = require('jsonwebtoken');
const crypto = require('crypto');

// Generate a random secret key
const secretKey = crypto.randomBytes(32).toString('hex');

const generateAuthToken = (userId) => {
    const token = jwt.sign({ userId }, secretKey, { expiresIn: '1h' });
    return token;
};

// Verify JWT token using random secret key
const verifyAuthToken = (token) => {
    try {
        const decoded = jwt.verify(token, secretKey);
        return decoded.userId;
    } catch (error) {
        throw new Error('Invalid token');
    }
};

module.exports = {
    generateAuthToken,
    verifyAuthToken
};
