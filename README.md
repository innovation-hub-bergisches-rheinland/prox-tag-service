# prox-tag-service

This service manages tags within the prox microservice environment

## Development

Please consider our [contributing guidelines](./CONTRIBUTING.md).

### Commit Messages

The commit messages **SHOULD** adhere to the
[Conventional Commits specification](https://conventionalcommits.org/). This
repository is also
[Commitizen](https://github.com/pocommitizen/cz-cli)-friendly. You can use
Commitizen seamless in this repository.

### Perform a release

In general releases are done by pushing a git tag which conforms to
[SemVer](https://semver.org/) specification. We prefix those tags with a `v`, so
the tag itself **MUST** follow the pattern `vMAJOR.MINOR.PATCH`. A label is not
used.

The simplest way to perform a release is by relying on our
[standard-version](https://github.com/conventional-changelog/standard-version)
configuration. If you are ready to perform a release simply call

```shell
$ npx standard-version
# or
$ npm run release
```

This will analyze the last commits since the last release, determine the new
version, generate a changelog and create a git tag for you. Next up you will
need to push the tag and version bumped files, our release pipeline will take
care of the rest.

```shell
$ git push --follow-tags origin main
```