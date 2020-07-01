#!/usr/bin/evn python
# -*- coding:utf-8 -*-

# FileName ScanProguard.py
# Author: HeyNiu
# Created Time: 2016/11/28
"""
扫描项目实体是否被混淆
1.传入项目路径，扫描出所有路径
2.符合实体类的路径存入列表
3.与混淆配置文件对比
4.打印被混淆的实体类
"""


import os
import time
import sys

temp = sys.argv[1]


class ScanProguard(object):

    def __init__(self, project):
        if not isinstance(project, dict):
            raise TypeError('Project object must be dict.')
        for value in project.values():
            if not os.path.exists(value):
                raise FileNotFoundError('%s' % value)
        self.project_path = project['project_path']
        self.proguard_path = project['proguard_path']
        self.nim_path = ''
        if 'nim_path' in project.keys():
            self.nim_path = project['nim_path']

    @staticmethod
    def __filter_dirs(target_path, dir_name):
        """
        获取目标地址目录下的文件夹列表
        :param dir_name:过滤文件夹名字
        :return:
        """
        return (os.path.join(root, d) for root, dirs, files in os.walk(target_path) for d in dirs if
                dir_name.upper() in d.upper())

    def __get_project_entity(self):
        """
        获取将要进行匹配的项目实体
        :return:
        """
        files = self.__filter_dirs(self.project_path, 'Entity')
        return [str(i.split('\java\\')[-1]).replace('\\', '.') for i in files]

    def __get_nim_entity(self):
        """
        获取将要进行匹配的nim实体
        :return:
        """
        files = self.__filter_dirs(self.nim_path, 'Entity')
        return [str(i.split('\java\\')[-1]).replace('\\', '.') for i in files]

    def __get_proguard_entity(self):
        """
        获取将要进行匹配的项目混淆文件实体
        :return:
        """
        l = open(self.proguard_path, encoding='utf-8').readlines()
        return [i.replace('-keep class ', '').replace('.** {*;}', '').replace('.**  {*;}', '').strip() for i in l
                if 'ENTITY' in i.upper() and '-KEEP CLASS' in i.upper()]

    def print_entity(self):
        """
        打印被混淆的实体类
        :return:
        """
        project_entity = self.__get_project_entity()
        proguard_entity = self.__get_proguard_entity()
        nim_entity = self.__get_nim_entity()
        for i in nim_entity:
            project_entity.append(i)
        l = set(project_entity).difference(set(proguard_entity))
        if len(l) > 0:
            print('Errors, has some entities be garble.')
            for i in l:
                print(i)
        else:
            print('Checked pass.')


class Project:
    """
    这里传的就是项目路径，为了方便写成了这样
    """
    document_path = os.path.join(os.path.expanduser('~'), 'Documents')
    jz = ''
    zf = ''
    zx = ''
    pro = 'proguard-project.pro'
    java = 'src\main\java'
    J = {
        'proguard_path': os.path.join(document_path, jz, jz, 'proguard-rules.pro'),
        'project_path': os.path.join(document_path, jz, jz, java, 's'),
        'nim_path': os.path.join(document_path, jz, 'im', java, 'com')
    }
    ZF = {
        'proguard_path': os.path.join(document_path, zf, zf, pro),
        'project_path': os.path.join(document_path, zf, zf, java, 'com'),
        'nim_path': os.path.join(document_path, zf, 'im', java, 'com')
    }
    XIU = {
        'proguard_path': os.path.join(document_path, zx, 'D', 'proguard-project.txt'),
        'project_path': os.path.join(document_path, zx, 'D', java, 'com')
    }


if __name__ == '__main__':
    start = time.clock()
    if '1' == temp:
        print('A项目')
        s = ScanProguard(Project.J)
        s.print_entity()
    if '2' == temp:
        print('B项目')
        s = ScanProguard(Project.ZF)
        s.print_entity()
    if '3' == temp:
        print('C项目')
        s = ScanProguard(Project.XIU)
        s.print_entity()
    end = time.clock()
    print("Time: %.02f s" % (end - start))