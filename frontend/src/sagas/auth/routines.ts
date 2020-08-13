import { createRoutine } from 'redux-saga-routines';

export const loginRoutine = createRoutine('SIGN_IN:LOGIN');
export const registerRoutine = createRoutine('SIGN_UP:REGISTER');
export const logoutRoutine = createRoutine('LOG_OUT:LOGOUT');
export const getUserRoutine = createRoutine('USER:GET');
export const sendEmailToResetPasswordRoutine = createRoutine('USER:SEND_EMAIL_RESET_PASSWORD');
export const resetPasswordRoutine = createRoutine('USER:RESET_PASSWORD');

