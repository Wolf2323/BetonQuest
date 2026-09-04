// For this script to work on GitHub Pages, you need to ensure CORS is set up correctly on your Reposilite server.
// For local testing, you need to disable CORS in your browser,
// and you need to set the repoUrl to the actual URL of your Reposilite server including the target repository
// (like in your pom.xml).
// E.g.: https://repo.betonquest.org/betonquest/

document$.subscribe(async () => {
  const repoUrl = "https://repo.betonquest.org/betonquest/";
  const parts = repoUrl.split("/");
  const baseUrl = parts.slice(0, -2).join("/") + "/";

  window.onload = async function () {
    let urlParams = new URLSearchParams(window.location.search);
    const path = urlParams.get("path");
    if (path) {
      const url = repoUrl + path;
      const filename = urlParams.get("filename");
      await downloadWithRename(url, filename);
      window.location.href = window.location.href.split("?")[0];
    }
  };

  await showBuilds();

  async function showBuilds() {
    getBuilds("?snapshots=false").then(builds =>
      loadBuilds("release-build", builds));
    getBuilds("?releases=false&limit=100", true).then(builds =>
      loadBuilds("development-build", builds));
  }

  async function loadBuilds(idKey, builds) {
    const latestBuild = document.getElementsByClassName("download-latest-" + idKey)[0];
    if (builds.length > 0) {
      const version = builds[0].version;
      latestBuild.textContent = version;
      const downloadUrl = builds[0].downloadUrl;
      latestBuild.onclick = function () {
        downloadWithRename(downloadUrl, "BetonQuest-" + version + ".jar", latestBuild);
      };
      resetDisabled(latestBuild);
    } else {
      latestBuild.textContent = "Nothing was Found";
    }
    builds.shift();

    const buildList = document.getElementById("download-all-" + idKey);
    resetDisabled(buildList.parentNode);
    await loadAllBuilds(builds, buildList);
  }

  async function loadAllBuilds(builds, buildList) {
    if (builds.length > 0) {
      const ul = document.createElement("ul");
      buildList.appendChild(ul);
      for (const build of builds) {
        const li = document.createElement("li");
        li.style.cssText = "padding: 0";
        const a = document.createElement("a");
        const version = build.version;
        a.textContent = version;
        a.href = "#";
        a.onclick = function () {
          downloadWithRename(build.downloadUrl, "BetonQuest-" + version + ".jar", a);
        };
        a.style.cssText = "width: 100%; text-align: center;";
        a.classList.add("md-button");
        a.classList.add("md-button--secondary");
        li.appendChild(a);
        ul.appendChild(li);
      }
    }
  }

  function resetDisabled(element) {
    element.style.pointerEvents = "auto";
    element.style.opacity = "1";
  }

  async function getBuilds(filter, firstGroupOnly = false) {
    const builds = [];
    try {
      let data = await fetch(baseUrl + `api/pommapper/id/BetonQuest` + filter)
        .then(response => response.json());
      for (const group of data) {
        for (const versionEntry of group["versions"]) {
          let pluginVersion = versionEntry["entries"]["pluginVersion"];
          let betonquestVersion = versionEntry["entries"]["betonquestVersion"];
          let downloadUrl = repoUrl + versionEntry["jar"].replace(".jar", "-shaded.jar");
          builds.push({version: pluginVersion ? pluginVersion : betonquestVersion, downloadUrl: downloadUrl});
        }
        if (firstGroupOnly) {
          break;
        }
      }
    } catch (error) {
      console.error("Failed to fetch builds:", error);
    }
    return builds;
  }

  async function downloadWithRename(url, filename, statusElement) {
    const originalText = statusElement ? statusElement.textContent : "";
    if (statusElement) {
      statusElement.style.pointerEvents = "none";
      statusElement.textContent = "Downloading...";
    }
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(`${response.status} ${response.statusText}`);

      const contentLength = response.headers.get("Content-Length");
      const total = contentLength ? parseInt(contentLength, 10) : 0;
      let loaded = 0;

      const reader = response.body.getReader();
      const chunks = [];

      while (true) {
        const {done, value} = await reader.read();
        if (done) break;
        chunks.push(value);
        loaded += value.length;

        if (statusElement) {
          if (total) {
            const percent = Math.round((loaded / total) * 100);
            statusElement.textContent = `Downloading... ${percent}%`;
          } else {
            const loadedMb = (loaded / (1024 * 1024)).toFixed(1);
            statusElement.textContent = `Downloading... ${loadedMb} MB`;
          }
        }
      }

      const blob = new Blob(chunks);
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = filename || url.split("/").pop();
      link.click();
      URL.revokeObjectURL(link.href);
    } catch (error) {
      console.error("Download failed:", error);
      if (statusElement) {
        statusElement.textContent = "Download failed";
      }
    } finally {
      if (statusElement) {
        setTimeout(() => {
          statusElement.textContent = originalText;
          resetDisabled(statusElement);
        }, 1500);
      }
    }
  }

});
