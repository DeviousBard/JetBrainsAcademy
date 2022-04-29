from dataclasses import dataclass


class GlowingBacteria:

    @dataclass
    class Plasmid:
        strand_orig: str
        strand_restrict_site: str
        gfp_orig: str
        gfp_orig_restrict_left: str
        gfp_orig_restrict_right: str

    def __init__(self):
        self.complements = {
            "A": "T",
            "C": "G",
            "T": "A",
            "G": "C"
        }

    @staticmethod
    def get_user_input(prompt):
        response = input(f"{prompt}")
        return response

    def get_complement(self, strand):
        complement = ''
        for i in strand:
            complement += self.complements[i]
        return complement

    @staticmethod
    def split_original_restriction_site(strand, start_sequence, is_complement):
        location = strand.find(start_sequence) + (5 if is_complement else 1)
        split = (strand[:location], strand[location:])
        return split

    @staticmethod
    def split_gfp_restriction_site(strand, start_sequence, end_sequence, is_complement):
        location = strand.find(start_sequence) + (5 if is_complement else 1)
        beginning = strand[location:]
        location = beginning.find(end_sequence) + (5 if is_complement else 1)
        end = beginning[0:location]
        return end

    @staticmethod
    def read_plasmid_data(filename):
        file = open(filename, 'r')
        strand_orig = file.readline().replace('\n', '')
        strand_orig_restrict_site = file.readline().replace('\n', '')
        gfp_orig = file.readline().replace('\n', '')
        gfp_orig_restrict_ends = file.readline().replace('\n', '').split(" ")
        gfp_orig_restrict_left = gfp_orig_restrict_ends[0]
        gfp_orig_restrict_right = gfp_orig_restrict_ends[1]

        plasmid = GlowingBacteria.Plasmid(
            strand_orig=strand_orig,
            strand_restrict_site=strand_orig_restrict_site,
            gfp_orig=gfp_orig,
            gfp_orig_restrict_left=gfp_orig_restrict_left,
            gfp_orig_restrict_right=gfp_orig_restrict_right
        )
        file.close()
        return plasmid

    def print_glowing_plasmid(self, plasmid):
        strand_orig = plasmid.strand_orig
        strand_orig_split = self.split_original_restriction_site(strand_orig, plasmid.strand_restrict_site, False)
        gfp_orig = plasmid.gfp_orig
        gfp_orig_restrict_left = plasmid.gfp_orig_restrict_left
        gfp_orig_restrict_right = plasmid.gfp_orig_restrict_right
        gfp_restrict = self.split_gfp_restriction_site(gfp_orig, gfp_orig_restrict_left, gfp_orig_restrict_right, False)
        print(strand_orig_split[0] + gfp_restrict + strand_orig_split[1])

        strand_comp = self.get_complement(strand_orig)
        strand_comp_restrict_site = self.get_complement(plasmid.strand_restrict_site)
        strand_comp_split = self.split_original_restriction_site(strand_comp, strand_comp_restrict_site, True)
        gfp_comp = self.get_complement(gfp_orig)
        gfp_comp_restrict_left = self.get_complement(gfp_orig_restrict_left)
        gfp_comp_restrict_right = self.get_complement(gfp_orig_restrict_right)
        gfp_comp_restrict = self.split_gfp_restriction_site(gfp_comp, gfp_comp_restrict_left,
                                                            gfp_comp_restrict_right, True)
        print(strand_comp_split[0] + gfp_comp_restrict + strand_comp_split[1])

    def run(self):
        file_name = self.get_user_input("")
        plasmid = self.read_plasmid_data(file_name)
        self.print_glowing_plasmid(plasmid)


def main():
    GlowingBacteria().run()


if __name__ == "__main__":
    main()
