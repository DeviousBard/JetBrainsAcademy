import json


class EasyRider:

    def __init__(self):
        self.bus_stop_data = []

    def read_bus_stops_file(self):
        self.bus_stop_data = json.loads(input())
        regular_stop_names = set()
        for regular_stop in list(filter(lambda bus_info: bus_info["stop_type"] in ["", "S", "F"], self.bus_stop_data)):
            regular_stop_names.add(regular_stop["stop_name"])
        on_demand_stop_names = set()
        for on_demand_stop in list(filter(lambda bus_info: bus_info["stop_type"] == "O", self.bus_stop_data)):
            on_demand_stop_names.add(on_demand_stop["stop_name"])
        print("On demand stops test:")
        errors = sorted(list(regular_stop_names & on_demand_stop_names))
        if len(errors) > 0:
            print(f"Wrong stop type: {errors}")
        else:
            print("OK")

    def process_bus_stop_data(self):
        self.read_bus_stops_file()


EasyRider().process_bus_stop_data()
