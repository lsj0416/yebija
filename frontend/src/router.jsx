import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import TemplatePage from './pages/TemplatePage';
import TemplateFormPage from './pages/TemplateFormPage';
import PrivateRoute from './components/PrivateRoute';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <PrivateRoute><Navigate to="/templates" replace /></PrivateRoute> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },

      // Step 3: 템플릿 관리
      { path: 'templates', element: <PrivateRoute><TemplatePage /></PrivateRoute> },
      { path: 'templates/new', element: <PrivateRoute><TemplateFormPage /></PrivateRoute> },
      { path: 'templates/:id/edit', element: <PrivateRoute><TemplateFormPage /></PrivateRoute> },

      // Step 4: 예배 생성
      // { path: 'worships', element: <PrivateRoute><WorshipPage /></PrivateRoute> },
      // { path: 'worships/:id', element: <PrivateRoute><WorshipDetailPage /></PrivateRoute> },
    ],
  },
]);

export default router;
