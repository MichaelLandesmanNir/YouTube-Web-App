const fs = require('fs');
const path = require('path');

const logStream = fs.createWriteStream(path.join(__dirname, 'access.log'), { flags: 'a' });

const loggerMiddleware = (req, res, next) => {
    const startTime = Date.now();
    
    // Build the log entry as the request starts
    const { method, url } = req;
    const userIp = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    
    // Wait until the response has finished
    res.on('finish', () => {
        const { statusCode } = res;
        const contentLength = res.get('Content-Length') || 0;
        const endTime = Date.now();
        const responseTime = endTime - startTime;

        // Create the log entry with all necessary details
        const logEntry = `${method} ${url} ${statusCode} ${contentLength} - ${responseTime}ms - ${userIp}`;

        // Write to access.log file
        logStream.write(logEntry);
        
        console.log(logEntry);

        // Optionally, you could also console.log the entry if needed for immediate viewing
        // console.log(logEntry);
    });

    next();
};

module.exports = loggerMiddleware;