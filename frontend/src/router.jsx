import { createBrowserRouter } from 'react-router-dom';
import App from './App';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      // Step 2: 로그인·회원가입
      // { path: 'login', element: <LoginPage /> },
      // { path: 'register', element: <RegisterPage /> },

      // Step 3: 템플릿 관리
      // { path: 'templates', element: <TemplatePage /> },

      // Step 4: 예배 생성
      // { path: 'worships', element: <WorshipPage /> },
      // { path: 'worships/:id', element: <WorshipDetailPage /> },
    ],
  },
]);

export default router;
