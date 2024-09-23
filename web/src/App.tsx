import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import './App.css';
import { ActorDetail, Home } from '@/pages';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Home />,
  },
  {
    path: '/actor/detail',
    element: <ActorDetail />,
  },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
