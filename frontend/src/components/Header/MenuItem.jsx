import styles from './MenuButton.module.css';

export const MenuItem = ({title, progress, reward}) => {
    return (
        <div className={styles.menuListItem}>
            <div className={styles.itemContent}>
                <svg className={styles.iconExclamation} width="32"
                     height="32"
                     viewBox="0 0 32 32"
                     fill="none"
                     xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        d="M16 28.6666C17.1046 28.6666 18 27.7712 18 26.6666C18 25.5621 17.1046 24.6666 16 24.6666C14.8954 24.6666 14 25.5621 14 26.6666C14 27.7712 14.8954 28.6666 16 28.6666Z"
                        fill="#7F5C5C"
                    />
                    <path
                        d="M15.8666 22H16.1333C16.4887 21.9852 16.8266 21.8419 17.0841 21.5966C17.3416 21.3513 17.5013 21.0208 17.5333 20.6666L18.6666 4.86662C18.6666 4.15937 18.3857 3.4811 17.8856 2.981C17.3855 2.4809 16.7072 2.19995 16 2.19995C15.2927 2.19995 14.6145 2.4809 14.1144 2.981C13.6143 3.4811 13.3333 4.15937 13.3333 4.86662L14.4666 20.6666C14.4987 21.0208 14.6583 21.3513 14.9159 21.5966C15.1734 21.8419 15.5113 21.9852 15.8666 22Z"
                        fill="#7F5C5C"
                    />
                </svg>
                <div className={styles.textWrapper}>
                    <div className={styles.labelText}>{title}</div>
                    <div className={styles.progress}>{progress}</div>
                </div>
                <div className={styles.rewardWrapper}>
                    <svg className={styles.iconCoin} width="20"
                         height="20"
                         viewBox="0 0 20 20"
                         fill="none"
                         xmlns="http://www.w3.org/2000/svg"
                    >
                        <path d="M7.5 0H6.25V1.25H7.5V0Z" fill="black"/>
                        <path d="M8.75 0H7.5V1.25H8.75V0Z" fill="black"/>
                        <path d="M10 0H8.75V1.25H10V0Z" fill="black"/>
                        <path d="M11.25 0H10V1.25H11.25V0Z" fill="black"/>
                        <path d="M12.5 0H11.25V1.25H12.5V0Z" fill="black"/>
                        <path d="M13.75 0H12.5V1.25H13.75V0Z" fill="black"/>
                        <path d="M5 1.25H3.75V2.5H5V1.25Z" fill="black"/>
                        <path d="M6.25 1.25H5V2.5H6.25V1.25Z" fill="black"/>
                        <path d="M7.5 1.25H6.25V2.5H7.5V1.25Z" fill="#FFB039"/>
                        <path d="M8.75 1.25H7.5V2.5H8.75V1.25Z" fill="#FFB039"/>
                        <path d="M10 1.25H8.75V2.5H10V1.25Z" fill="#FFB039"/>
                        <path d="M11.25 1.25H10V2.5H11.25V1.25Z" fill="#FFB039"/>
                        <path d="M12.5 1.25H11.25V2.5H12.5V1.25Z" fill="#FFB039"/>
                        <path d="M13.75 1.25H12.5V2.5H13.75V1.25Z" fill="#FFB039"/>
                        <path d="M15 1.25H13.75V2.5H15V1.25Z" fill="black"/>
                        <path d="M16.25 1.25H15V2.5H16.25V1.25Z" fill="black"/>
                        <path d="M3.75 2.5H2.5V3.75H3.75V2.5Z" fill="black"/>
                        <path d="M5 2.5H3.75V3.75H5V2.5Z" fill="#FFB039"/>
                        <path d="M6.25 2.5H5V3.75H6.25V2.5Z" fill="#FFB039"/>
                        <path d="M7.5 2.5H6.25V3.75H7.5V2.5Z" fill="#FFFFFB"/>
                        <path d="M8.75 2.5H7.5V3.75H8.75V2.5Z" fill="#FFE85E"/>
                        <path d="M10 2.5H8.75V3.75H10V2.5Z" fill="#FFE85E"/>
                        <path d="M11.25 2.5H10V3.75H11.25V2.5Z" fill="#FFE85E"/>
                        <path d="M12.5 2.5H11.25V3.75H12.5V2.5Z" fill="#FFE85E"/>
                        <path d="M13.75 2.5H12.5V3.75H13.75V2.5Z" fill="#FFE85E"/>
                        <path d="M15 2.5H13.75V3.75H15V2.5Z" fill="#DB7103"/>
                        <path d="M16.25 2.5H15V3.75H16.25V2.5Z" fill="#DB7103"/>
                        <path d="M17.5 2.5H16.25V3.75H17.5V2.5Z" fill="black"/>
                        <path d="M2.5 3.75H1.25V5H2.5V3.75Z" fill="black"/>
                        <path d="M3.75 3.75H2.5V5H3.75V3.75Z" fill="#FFB039"/>
                        <path d="M5 3.75H3.75V5H5V3.75Z" fill="#FFFFFB"/>
                        <path d="M6.25 3.75H5V5H6.25V3.75Z" fill="#FFFFFB"/>
                        <path d="M7.5 3.75H6.25V5H7.5V3.75Z" fill="#FFE85E"/>
                        <path d="M8.75 3.75H7.5V5H8.75V3.75Z" fill="#DB7103"/>
                        <path d="M10 3.75H8.75V5H10V3.75Z" fill="#DB7103"/>
                        <path d="M11.25 3.75H10V5H11.25V3.75Z" fill="#DB7103"/>
                        <path d="M12.5 3.75H11.25V5H12.5V3.75Z" fill="#DB7103"/>
                        <path d="M13.75 3.75H12.5V5H13.75V3.75Z" fill="#FFE85E"/>
                        <path d="M15 3.75H13.75V5H15V3.75Z" fill="#FFE85E"/>
                        <path d="M16.25 3.75H15V5H16.25V3.75Z" fill="#FFE85E"/>
                        <path d="M17.5 3.75H16.25V5H17.5V3.75Z" fill="#DB7103"/>
                        <path d="M18.75 3.75H17.5V5H18.75V3.75Z" fill="black"/>
                        <path d="M2.5 5H1.25V6.25H2.5V5Z" fill="black"/>
                        <path d="M3.75 5H2.5V6.25H3.75V5Z" fill="#FFB039"/>
                        <path d="M5 5H3.75V6.25H5V5Z" fill="#FFFFFB"/>
                        <path d="M6.25 5H5V6.25H6.25V5Z" fill="#DB7103"/>
                        <path d="M7.5 5H6.25V6.25H7.5V5Z" fill="#DB7103"/>
                        <path d="M8.75 5H7.5V6.25H8.75V5Z" fill="#FFB039"/>
                        <path d="M10 5H8.75V6.25H10V5Z" fill="#FFB039"/>
                        <path d="M11.25 5H10V6.25H11.25V5Z" fill="#FFB039"/>
                        <path d="M12.5 5H11.25V6.25H12.5V5Z" fill="#FFB039"/>
                        <path d="M13.75 5H12.5V6.25H13.75V5Z" fill="#DB7103"/>
                        <path d="M15 5H13.75V6.25H15V5Z" fill="#DB7103"/>
                        <path d="M16.25 5H15V6.25H16.25V5Z" fill="#FFE85E"/>
                        <path d="M17.5 5H16.25V6.25H17.5V5Z" fill="#DB7103"/>
                        <path d="M18.75 5H17.5V6.25H18.75V5Z" fill="black"/>
                        <path d="M1.25 6.25H0V7.5H1.25V6.25Z" fill="black"/>
                        <path d="M2.5 6.25H1.25V7.5H2.5V6.25Z" fill="#FFB039"/>
                        <path d="M3.75 6.25H2.5V7.5H3.75V6.25Z" fill="#FFFFFB"/>
                        <path d="M5 6.25H3.75V7.5H5V6.25Z" fill="#FFE85E"/>
                        <path d="M6.25 6.25H5V7.5H6.25V6.25Z" fill="#DB7103"/>
                        <path d="M7.5 6.25H6.25V7.5H7.5V6.25Z" fill="#FFB039"/>
                        <path d="M8.75 6.25H7.5V7.5H8.75V6.25Z" fill="#FFB039"/>
                        <path d="M10 6.25H8.75V7.5H10V6.25Z" fill="#FFB039"/>
                        <path d="M11.25 6.25H10V7.5H11.25V6.25Z" fill="#FFB039"/>
                        <path d="M12.5 6.25H11.25V7.5H12.5V6.25Z" fill="#FFE85E"/>
                        <path d="M13.75 6.25H12.5V7.5H13.75V6.25Z" fill="#FFE85E"/>
                        <path d="M15 6.25H13.75V7.5H15V6.25Z" fill="#DB7103"/>
                        <path d="M16.25 6.25H15V7.5H16.25V6.25Z" fill="#FFE85E"/>
                        <path d="M17.5 6.25H16.25V7.5H17.5V6.25Z" fill="#FFE85E"/>
                        <path d="M18.75 6.25H17.5V7.5H18.75V6.25Z" fill="#DB7103"/>
                        <path d="M20 6.25H18.75V7.5H20V6.25Z" fill="black"/>
                        <path d="M1.25 7.5H0V8.75H1.25V7.5Z" fill="black"/>
                        <path d="M2.5 7.5H1.25V8.75H2.5V7.5Z" fill="#FFB039"/>
                        <path d="M3.75 7.5H2.5V8.75H3.75V7.5Z" fill="#FFE85E"/>
                        <path d="M5 7.5H3.75V8.75H5V7.5Z" fill="#DB7103"/>
                        <path d="M6.25 7.5H5V8.75H6.25V7.5Z" fill="#FFB039"/>
                        <path d="M7.5 7.5H6.25V8.75H7.5V7.5Z" fill="#FFB039"/>
                        <path d="M8.75 7.5H7.5V8.75H8.75V7.5Z" fill="#FFB039"/>
                        <path d="M10 7.5H8.75V8.75H10V7.5Z" fill="#FFE85E"/>
                        <path d="M11.25 7.5H10V8.75H11.25V7.5Z" fill="#FFE85E"/>
                        <path d="M12.5 7.5H11.25V8.75H12.5V7.5Z" fill="#FFE85E"/>
                        <path d="M13.75 7.5H12.5V8.75H13.75V7.5Z" fill="#FFE85E"/>
                        <path d="M15 7.5H13.75V8.75H15V7.5Z" fill="#FFE85E"/>
                        <path d="M16.25 7.5H15V8.75H16.25V7.5Z" fill="#FFB039"/>
                        <path d="M17.5 7.5H16.25V8.75H17.5V7.5Z" fill="#FFE85E"/>
                        <path d="M18.75 7.5H17.5V8.75H18.75V7.5Z" fill="#DB7103"/>
                        <path d="M20 7.5H18.75V8.75H20V7.5Z" fill="black"/>
                        <path d="M1.25 8.75H0V10H1.25V8.75Z" fill="black"/>
                        <path d="M2.5 8.75H1.25V10H2.5V8.75Z" fill="#FFB039"/>
                        <path d="M3.75 8.75H2.5V10H3.75V8.75Z" fill="#FFE85E"/>
                        <path d="M5 8.75H3.75V10H5V8.75Z" fill="#DB7103"/>
                        <path d="M6.25 8.75H5V10H6.25V8.75Z" fill="#FFB039"/>
                        <path d="M7.5 8.75H6.25V10H7.5V8.75Z" fill="#FFB039"/>
                        <path d="M8.75 8.75H7.5V10H8.75V8.75Z" fill="#FFE85E"/>
                        <path d="M10 8.75H8.75V10H10V8.75Z" fill="#FFE85E"/>
                        <path d="M11.25 8.75H10V10H11.25V8.75Z" fill="#FFE85E"/>
                        <path d="M12.5 8.75H11.25V10H12.5V8.75Z" fill="#FFE85E"/>
                        <path d="M13.75 8.75H12.5V10H13.75V8.75Z" fill="#FFE85E"/>
                        <path d="M15 8.75H13.75V10H15V8.75Z" fill="#FFFFFB"/>
                        <path d="M16.25 8.75H15V10H16.25V8.75Z" fill="#FFB039"/>
                        <path d="M17.5 8.75H16.25V10H17.5V8.75Z" fill="#FFE85E"/>
                        <path d="M18.75 8.75H17.5V10H18.75V8.75Z" fill="#DB7103"/>
                        <path d="M20 8.75H18.75V10H20V8.75Z" fill="black"/>
                        <path d="M1.25 10H0V11.25H1.25V10Z" fill="black"/>
                        <path d="M2.5 10H1.25V11.25H2.5V10Z" fill="#FFB039"/>
                        <path d="M3.75 10H2.5V11.25H3.75V10Z" fill="#FFE85E"/>
                        <path d="M5 10H3.75V11.25H5V10Z" fill="#DB7103"/>
                        <path d="M6.25 10H5V11.25H6.25V10Z" fill="#FFB039"/>
                        <path d="M7.5 10H6.25V11.25H7.5V10Z" fill="#FFB039"/>
                        <path d="M8.75 10H7.5V11.25H8.75V10Z" fill="#FFE85E"/>
                        <path d="M10 10H8.75V11.25H10V10Z" fill="#FFE85E"/>
                        <path d="M11.25 10H10V11.25H11.25V10Z" fill="#FFE85E"/>
                        <path d="M12.5 10H11.25V11.25H12.5V10Z" fill="#FFE85E"/>
                        <path d="M13.75 10H12.5V11.25H13.75V10Z" fill="#FFE85E"/>
                        <path d="M15 10H13.75V11.25H15V10Z" fill="#FFFFFB"/>
                        <path d="M16.25 10H15V11.25H16.25V10Z" fill="#FFB039"/>
                        <path d="M17.5 10H16.25V11.25H17.5V10Z" fill="#FFE85E"/>
                        <path d="M18.75 10H17.5V11.25H18.75V10Z" fill="#DB7103"/>
                        <path d="M20 10H18.75V11.25H20V10Z" fill="black"/>
                        <path d="M1.25 11.25H0V12.5H1.25V11.25Z" fill="black"/>
                        <path d="M2.5 11.25H1.25V12.5H2.5V11.25Z" fill="#FFB039"/>
                        <path d="M3.75 11.25H2.5V12.5H3.75V11.25Z" fill="#FFE85E"/>
                        <path d="M5 11.25H3.75V12.5H5V11.25Z" fill="#DB7103"/>
                        <path d="M6.25 11.25H5V12.5H6.25V11.25Z" fill="#FFB039"/>
                        <path d="M7.5 11.25H6.25V12.5H7.5V11.25Z" fill="#FFE85E"/>
                        <path d="M8.75 11.25H7.5V12.5H8.75V11.25Z" fill="#FFE85E"/>
                        <path d="M10 11.25H8.75V12.5H10V11.25Z" fill="#FFE85E"/>
                        <path d="M11.25 11.25H10V12.5H11.25V11.25Z" fill="#FFE85E"/>
                        <path d="M12.5 11.25H11.25V12.5H12.5V11.25Z" fill="#FFE85E"/>
                        <path d="M13.75 11.25H12.5V12.5H13.75V11.25Z" fill="#FFE85E"/>
                        <path d="M15 11.25H13.75V12.5H15V11.25Z" fill="#FFFFFB"/>
                        <path d="M16.25 11.25H15V12.5H16.25V11.25Z" fill="#FFB039"/>
                        <path d="M17.5 11.25H16.25V12.5H17.5V11.25Z" fill="#FFE85E"/>
                        <path d="M18.75 11.25H17.5V12.5H18.75V11.25Z" fill="#DB7103"/>
                        <path d="M20 11.25H18.75V12.5H20V11.25Z" fill="black"/>
                        <path d="M1.25 12.5H0V13.75H1.25V12.5Z" fill="black"/>
                        <path d="M2.5 12.5H1.25V13.75H2.5V12.5Z" fill="#FFB039"/>
                        <path d="M3.75 12.5H2.5V13.75H3.75V12.5Z" fill="#FFE85E"/>
                        <path d="M5 12.5H3.75V13.75H5V12.5Z" fill="#FFE85E"/>
                        <path d="M6.25 12.5H5V13.75H6.25V12.5Z" fill="#DB7103"/>
                        <path d="M7.5 12.5H6.25V13.75H7.5V12.5Z" fill="#FFE85E"/>
                        <path d="M8.75 12.5H7.5V13.75H8.75V12.5Z" fill="#FFE85E"/>
                        <path d="M10 12.5H8.75V13.75H10V12.5Z" fill="#FFE85E"/>
                        <path d="M11.25 12.5H10V13.75H11.25V12.5Z" fill="#FFE85E"/>
                        <path d="M12.5 12.5H11.25V13.75H12.5V12.5Z" fill="#FFE85E"/>
                        <path d="M13.75 12.5H12.5V13.75H13.75V12.5Z" fill="#FFFFFB"/>
                        <path d="M15 12.5H13.75V13.75H15V12.5Z" fill="#FFB039"/>
                        <path d="M16.25 12.5H15V13.75H16.25V12.5Z" fill="#FFE85E"/>
                        <path d="M17.5 12.5H16.25V13.75H17.5V12.5Z" fill="#FFE85E"/>
                        <path d="M18.75 12.5H17.5V13.75H18.75V12.5Z" fill="#DB7103"/>
                        <path d="M20 12.5H18.75V13.75H20V12.5Z" fill="black"/>
                        <path d="M2.5 13.75H1.25V15H2.5V13.75Z" fill="black"/>
                        <path d="M3.75 13.75H2.5V15H3.75V13.75Z" fill="#FFB039"/>
                        <path d="M5 13.75H3.75V15H5V13.75Z" fill="#FFE85E"/>
                        <path d="M6.25 13.75H5V15H6.25V13.75Z" fill="#DB7103"/>
                        <path d="M7.5 13.75H6.25V15H7.5V13.75Z" fill="#DB7103"/>
                        <path d="M8.75 13.75H7.5V15H8.75V13.75Z" fill="#FFE85E"/>
                        <path d="M10 13.75H8.75V15H10V13.75Z" fill="#FFFFFB"/>
                        <path d="M11.25 13.75H10V15H11.25V13.75Z" fill="#FFFFFB"/>
                        <path d="M12.5 13.75H11.25V15H12.5V13.75Z" fill="#FFFFFB"/>
                        <path d="M13.75 13.75H12.5V15H13.75V13.75Z" fill="#FFB039"/>
                        <path d="M15 13.75H13.75V15H15V13.75Z" fill="#FFB039"/>
                        <path d="M16.25 13.75H15V15H16.25V13.75Z" fill="#FFE85E"/>
                        <path d="M17.5 13.75H16.25V15H17.5V13.75Z" fill="#DB7103"/>
                        <path d="M18.75 13.75H17.5V15H18.75V13.75Z" fill="black"/>
                        <path d="M2.5 15H1.25V16.25H2.5V15Z" fill="black"/>
                        <path d="M3.75 15H2.5V16.25H3.75V15Z" fill="#FFB039"/>
                        <path d="M5 15H3.75V16.25H5V15Z" fill="#FFE85E"/>
                        <path d="M6.25 15H5V16.25H6.25V15Z" fill="#FFE85E"/>
                        <path d="M7.5 15H6.25V16.25H7.5V15Z" fill="#FFE85E"/>
                        <path d="M8.75 15H7.5V16.25H8.75V15Z" fill="#FFB039"/>
                        <path d="M10 15H8.75V16.25H10V15Z" fill="#FFB039"/>
                        <path d="M11.25 15H10V16.25H11.25V15Z" fill="#FFB039"/>
                        <path d="M12.5 15H11.25V16.25H12.5V15Z" fill="#FFB039"/>
                        <path d="M13.75 15H12.5V16.25H13.75V15Z" fill="#FFE85E"/>
                        <path d="M15 15H13.75V16.25H15V15Z" fill="#FFE85E"/>
                        <path d="M16.25 15H15V16.25H16.25V15Z" fill="#FFE85E"/>
                        <path d="M17.5 15H16.25V16.25H17.5V15Z" fill="#DB7103"/>
                        <path d="M18.75 15H17.5V16.25H18.75V15Z" fill="black"/>
                        <path d="M3.75 16.25H2.5V17.5H3.75V16.25Z" fill="black"/>
                        <path d="M5 16.25H3.75V17.5H5V16.25Z" fill="#FFB039"/>
                        <path d="M6.25 16.25H5V17.5H6.25V16.25Z" fill="#FFB039"/>
                        <path d="M7.5 16.25H6.25V17.5H7.5V16.25Z" fill="#FFE85E"/>
                        <path d="M8.75 16.25H7.5V17.5H8.75V16.25Z" fill="#FFE85E"/>
                        <path d="M10 16.25H8.75V17.5H10V16.25Z" fill="#FFE85E"/>
                        <path d="M11.25 16.25H10V17.5H11.25V16.25Z" fill="#FFE85E"/>
                        <path d="M12.5 16.25H11.25V17.5H12.5V16.25Z" fill="#FFE85E"/>
                        <path d="M13.75 16.25H12.5V17.5H13.75V16.25Z" fill="#FFE85E"/>
                        <path d="M15 16.25H13.75V17.5H15V16.25Z" fill="#DB7103"/>
                        <path d="M16.25 16.25H15V17.5H16.25V16.25Z" fill="#DB7103"/>
                        <path d="M17.5 16.25H16.25V17.5H17.5V16.25Z" fill="black"/>
                        <path d="M5 17.5H3.75V18.75H5V17.5Z" fill="black"/>
                        <path d="M6.25 17.5H5V18.75H6.25V17.5Z" fill="black"/>
                        <path d="M7.5 17.5H6.25V18.75H7.5V17.5Z" fill="#FFB039"/>
                        <path d="M8.75 17.5H7.5V18.75H8.75V17.5Z" fill="#FFB039"/>
                        <path d="M10 17.5H8.75V18.75H10V17.5Z" fill="#FFB039"/>
                        <path d="M11.25 17.5H10V18.75H11.25V17.5Z" fill="#FFB039"/>
                        <path d="M12.5 17.5H11.25V18.75H12.5V17.5Z" fill="#FFB039"/>
                        <path d="M13.75 17.5H12.5V18.75H13.75V17.5Z" fill="#FFB039"/>
                        <path d="M15 17.5H13.75V18.75H15V17.5Z" fill="black"/>
                        <path d="M16.25 17.5H15V18.75H16.25V17.5Z" fill="black"/>
                        <path d="M7.5 18.75H6.25V20H7.5V18.75Z" fill="black"/>
                        <path d="M8.75 18.75H7.5V20H8.75V18.75Z" fill="black"/>
                        <path d="M10 18.75H8.75V20H10V18.75Z" fill="black"/>
                        <path d="M11.25 18.75H10V20H11.25V18.75Z" fill="black"/>
                        <path d="M12.5 18.75H11.25V20H12.5V18.75Z" fill="black"/>
                        <path d="M13.75 18.75H12.5V20H13.75V18.75Z" fill="black"/>
                    </svg>
                    <div className={styles.reward}>{reward}</div>
                </div>
            </div>
            <svg className={styles.divider} width="261" height="17" viewBox="0 0 261 17" fill="none">
                <line y1="8.5" x2="261" y2="8.5" stroke="#805C5C"/>
            </svg>
        </div>
    );
};