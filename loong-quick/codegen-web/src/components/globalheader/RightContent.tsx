import {QuestionCircleOutlined} from '@ant-design/icons';
import {Tooltip} from 'antd';
import React from 'react';
import Avatar from './AvatarDropdown';
import HeaderSearch from '../headersearch';
import SelectLang from '../selectlang';
import styles from './index.less';
import {getLoginUser} from '@/SessionManager';

export type SiderTheme = 'light' | 'dark';

export interface GlobalHeaderRightProps {
    theme?: SiderTheme;
    layout: 'side' | 'top' | 'mix';
}

const GlobalHeaderRight: React.FC<GlobalHeaderRightProps> = (props) => {
    const {theme, layout} = props;
    let className = styles.right;

    if (theme === 'dark' && layout === 'top') {
        className = `${styles.right}  ${styles.dark}`;
    }

    return (
        <div className={className}>
            <HeaderSearch
                className={`${styles.action} ${styles.search}`}
                placeholder="站内搜索"
                defaultValue="umi ui"
                options={[
                    {
                        label: <a href="https://umijs.org/zh/guide/umi-ui.html">umi ui</a>,
                        value: 'umi ui',
                    },
                    {
                        label: <a href="next.ant.design">Ant Design</a>,
                        value: 'Ant Design',
                    },
                    {
                        label: <a href="https://protable.ant.design/">Pro Table</a>,
                        value: 'Pro Table',
                    },
                    {
                        label: <a href="https://prolayout.ant.design/">Pro Layout</a>,
                        value: 'Pro Layout',
                    },
                ]}
                // onSearch={value => {
                //   //console.log('input', value);
                // }}
            />
            <Tooltip title="使用文档">
                <a
                    target="_blank"
                    href="https://pro.ant.design/docs/getting-started"
                    rel="noopener noreferrer"
                    className={styles.action}
                >
                    <QuestionCircleOutlined/>
                </a>
            </Tooltip>
            <Avatar currentUser={getLoginUser()}/>
            <SelectLang className={styles.action}/>
        </div>
    );
};

export default GlobalHeaderRight;