import sys
import os
import requests
from bs4 import BeautifulSoup
from bs4 import SoupStrainer

from colorama import init
from colorama import Fore, Back, Style


class BrowserExitException(Exception):
    pass


class BrowserError(Exception):
    pass


class Browser:

    # nytimes_com = '''
    # This New Liquid Is Magnetic, and Mesmerizing
    #
    # Scientists have created “soft” magnets that can flow
    # and change shape, and that could be a boon to medicine
    # and robotics. (Source: New York Times)
    #
    #
    # Most Wikipedia Profiles Are of Men. This Scientist Is Changing That.
    #
    # Jessica Wade has added nearly 700 Wikipedia biographies for
    #  important female and minority scientists in less than two
    #  years.
    #
    # '''
    #
    # bloomberg_com = '''
    # The Space Race: From Apollo 11 to Elon Musk
    #
    # It's 50 years since the world was gripped by historic images
    #  of Apollo 11, and Neil Armstrong -- the first man to walk
    #  on the moon. It was the height of the Cold War, and the charts
    #  were filled with David Bowie's Space Oddity, and Creedence's
    #  Bad Moon Rising. The world is a very different place than
    #  it was 5 decades ago. But how has the space race changed since
    #  the summer of '69? (Source: Bloomberg)
    #
    #
    # Twitter CEO Jack Dorsey Gives Talk at Apple Headquarters
    #
    # Twitter and Square Chief Executive Officer Jack Dorsey
    #  addressed Apple Inc. employees at the iPhone maker’s headquarters
    #  Tuesday, a signal of the strong ties between the Silicon Valley giants.
    # '''
    #
    def __init__(self, directory):
        self.directory = directory
        try:
            os.makedirs(directory)
        except FileExistsError:
            pass
        self.history = []
        self.current_url = None

    def __input_url__(self):
        url = input()
        if url == 'exit':
            raise BrowserExitException
        if url == 'back':
            url = self.get_history()
            while url == self.current_url:
                self.history.pop(0)
                url = self.get_history()
        return url

    def add_history(self, url):
        if len(self.history) == 0 or (len(self.history) > 0 and url not in self.history[0]):
            self.history.insert(0, url)

    def get_history(self):
        return self.history[0] if len(self.history) > 0 else None

    @staticmethod
    def __get_text_content__(html_content):
        tags = ['p', 'a', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'title']
        result = ''
        soup = BeautifulSoup(html_content, 'html.parser', parse_only=SoupStrainer(tags))
        for tag in soup:
            if tag.name == 'a':
                result += f"{Fore.BLUE + tag.text.strip()}\n"
            else:
                result += f"{tag.text.strip()}\n"
        return result

    @staticmethod
    def fetch_page(url):
        try:
            r = requests.get("https://" + url)
            if not r or not r.ok:
                return None
            return Browser.__get_text_content__(r.content)
        except requests.RequestException:
            return None

    def write_cache(self, url, text):
        file_name = Browser.__get_file_name_from_url__(url)
        f = open(self.directory + "/" + file_name, 'w')
        f.writelines(text)
        f.close()

    def read_cache(self, url):
        try:
            f = open(self.directory + "/" + url, 'r')
            result = f.read()
            f.close()
            return result
        except FileNotFoundError:
            return None

    def fetch(self, url=None):
        while not url:
            url = self.__input_url__() if not url else url
        if url.startswith("https://"):
            url = url[8:]
        result = self.read_cache(url)
        if not result:
            result = self.fetch_page(url)
            if result:
                self.write_cache(url, result)
        self.add_history(url)
        self.current_url = url
        return result

    @staticmethod
    def __get_file_name_from_url__(url):
        reversed_url = url[::-1]
        file_name = reversed_url[reversed_url.find('.') + 1:][::-1]
        return file_name


def main():
    # init()
    browser = Browser(sys.argv[1])
    try:
        while True:
            try:
                print(browser.fetch())
            except BrowserError as e:
                print(e)
    except BrowserExitException:
        pass


main()
