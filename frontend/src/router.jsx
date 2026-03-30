import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import AppLayout from './components/layout/AppLayout';
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
      // Public routes
      { path: 'login',    element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },

      // Authenticated routes — AppLayout shell
      {
        element: <PrivateRoute><AppLayout /></PrivateRoute>,
        children: [
          { index: true,                element: <Navigate to="/templates" replace /> },
          { path: 'dashboard',          element: <div style={{padding:'2rem',color:'var(--on-surface-variant)'}}>Dashboard — 준비 중</div> },

          // Step 3: 템플릿 관리
          { path: 'templates',          element: <TemplatePage /> },
          { path: 'templates/new',      element: <TemplateFormPage /> },
          { path: 'templates/:id/edit', element: <TemplateFormPage /> },

          // Step 4: 예배 생성 (coming)
          // { path: 'worships',           element: <WorshipPage /> },
          // { path: 'worships/:id',       element: <WorshipDetailPage /> },
        ],
      },
    ],
  },
]);

export default router;
