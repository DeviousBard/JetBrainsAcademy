from bs4 import BeautifulSoup

import requests

import sys


class NetworkConnectivityError(Exception):
    pass


class WordNotFoundError(Exception):
    pass


class Translator:
    languages = ["All", "Arabic", "German", "English", "Spanish", "French", "Hebrew",
                 "Japanese", "Dutch", "Polish", "Portuguese", "Romanian",
                 "Russian", "Turkish"]

    def __init__(self, from_lang=3, to_lang=4, word='hello'):

        self.from_lang = Translator.languages[from_lang]
        self.to_lang = Translator.languages[to_lang]
        self.word = word

    @classmethod
    def from_input(cls):
        print(f"Hello, you're welcome to the translator. Translator supports:")
        print("1. Arabic")
        print("2. German")
        print("3. English")
        print("4. Spanish")
        print("5. French")
        print("6. Hebrew")
        print("7. Japanese")
        print("8. Dutch")
        print("9. Polish")
        print("10. Portuguese")
        print("11. Romanian")
        print("12. Russian")
        print("13. Turkish")
        print("Type the number of your language:")
        from_lang = input()
        print("Type the number of language you want to translate to:")
        to_lang = input()
        print("Type the word you want to translate:")
        word = input()
        return Translator(int(from_lang), int(to_lang), word)

    @staticmethod
    def __fetch_translations__(from_lang, to_lang, word):
        trans_word_list = []
        trans_phrase_list = []
        r = None
        try:
            url = f"https://context.reverso.net/translation/" \
                  f"{from_lang.lower()}-{to_lang.lower()}/{word}"
            r = requests.get(url, headers={'User-Agent': 'Mozilla/5.0'})
        except Exception:
            raise NetworkConnectivityError
        if r.status_code == 200:
            soup = BeautifulSoup(r.content, 'html.parser')
            trans_words = soup.find_all("a", class_='dict')
            for trans_word in trans_words:
                trans_word_list.append(trans_word.text.strip())
            trans_phrases = soup.find_all("div", class_="ltr")
            for trans_phrase in trans_phrases:
                trans_phrase = trans_phrase.text.strip().replace('[', "").replace("]", "")
                trans_phrase_list.append(trans_phrase)
            trans_phrase_list = trans_phrase_list[2:]
            if len(trans_word_list) == 0:
                raise WordNotFoundError
        elif r.status_code == 404:
            raise WordNotFoundError
        else:
            raise NetworkConnectivityError
        return {"trans_word_list": trans_word_list, "trans_phrase_list": trans_phrase_list}

    def __print_translations__(self):
        translations = Translator.__fetch_translations__(self.from_lang, self.to_lang, self.word)
        trans_word_list = translations["trans_word_list"]
        trans_phrase_list = translations["trans_phrase_list"]
        print()
        print("Context examples:")
        print()
        print(f"{self.to_lang} Translations:")
        for word in trans_word_list:
            print(word)
        print()
        print(f"{self.to_lang} Examples:")
        idx = 0
        for phrase in trans_phrase_list:
            print(f"{phrase}{':' if idx % 2 == 0 else ''}")
            if idx % 2 == 1:
                print()
            idx += 1

    def __save_translations_to_file__(self):
        result = ""
        for to_lang in Translator.languages:
            if to_lang != 'All' and to_lang != self.from_lang:
                translations = Translator.__fetch_translations__(self.from_lang, to_lang, self.word)
                trans_word_list = translations["trans_word_list"]
                trans_phrase_list = translations["trans_phrase_list"]
                result += f'{to_lang} Translations:\n'
                for word in trans_word_list:
                    result += f'{word}\n'
                result += "\n"
                result += f'{to_lang} Examples:\n'
                idx = 0
                for phrase in trans_phrase_list:
                    result += f"{phrase}{':' if idx % 2 == 0 else ''}\n"
                    if idx % 2 == 1:
                        result += "\n"
                    idx += 1
                result += "\n"
                result += "\n"
        print(result)
        f = open(f"{self.word}.txt", 'w')
        f.write(result)
        f.close()

    def translate(self):
        if self.to_lang == 'All':
            self.__save_translations_to_file__()
        else:
            self.__print_translations__()


def main():
    args = sys.argv
    word = None
    try:
        if len(args) > 1:
            if args[1].capitalize() not in Translator.languages:
                print(f"Sorry, the program doesn't support {args[1]}")
                exit()
            from_lang = Translator.languages.index(args[1].capitalize())
            if args[2].capitalize() not in Translator.languages:
                print(f"Sorry, the program doesn't support {args[2]}")
                exit()
            to_lang = Translator.languages.index(args[2].capitalize())
            word = args[3]
            translator = Translator(from_lang, to_lang, word)
        else:
            translator = Translator.from_input()
        translator.translate()
    except WordNotFoundError:
        print(f"Sorry, unable to find {word}")
    except NetworkConnectivityError:
        print(f"Something wrong with your internet connection")



main()
