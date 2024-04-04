const PROXY_CONFIG = [
  {
    context: ['/api/**'],
    target: 'http://localhost:8080/api/courses',
    pathRewrite: {'^/api': ''},
    secure: false
  }
];

module.exports = PROXY_CONFIG;
