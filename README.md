Projeto baseado em https://loiane.training/continuar-curso/crud-angular-spring com adaptações para deixar 100% funcional:

a) No frontend, arquivo proxy.conf.js -> Adaptar o proxy com  context: ['/api/**'], e pathRewrite: {'^/api': ''};
b) No backend, arquivo CourseService.java -> Criar opções de CRUD pras lessons do curso manipulado;
c) No backend, arquivo application.properties -> Comentando o banco de dados h2 baseado em memória e adicionando baseado em arquivo 
