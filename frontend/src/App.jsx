import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/Layout/Layout';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import RoomList from './components/HotelRooms/RoomList';
import RoomDetails from './components/HotelRooms/RoomDetails';
import ReservationList from './components/Reservations/ReservationList';
import ProfileForm from './components/Users/ProfileForm';

const theme = createTheme();

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <AuthProvider>
          <Router>
            <Layout>
              <Routes>
                <Route path="/" element={<RoomList />} />
                <Route path="/login" element={<LoginForm />} />
                <Route path="/register" element={<RegisterForm />} />
                <Route path="/rooms/:id" element={<RoomDetails />} />
                <Route path="/reservations" element={<ReservationList />} />
                <Route path="/profile" element={<ProfileForm />} />
              </Routes>
            </Layout>
          </Router>
        </AuthProvider>
      </LocalizationProvider>
    </ThemeProvider>
  );
}

export default App;