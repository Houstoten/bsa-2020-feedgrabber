export interface IUserState {
    isLoading?: boolean;
    info?: IUserInfo;
    error?: IUserErrors;
    shortInfo?: IUserShort;
    settings?: IUserSettings;
}

export interface IUserInfo {
    id: string;
    userName: string;
    companyName: string;
    email: string;
    companyId: string;
    firstName?: string;
    lastName?: string;
    phoneNumber?: string;
    avatar?: string;
    role: string;
    roleId?: string;
}

export interface IUserErrors {
    login: string;
    register: string;
    getUser: string;
}

export interface IUserShort {
    id: string;
    avatar?: string;
    username: string;
    firstName?: string;
    lastName?: string;
    selected?: boolean;
    loading?: boolean;
}

export interface IUserSettings {
    language: string;
    enableNotifications: boolean;
}

